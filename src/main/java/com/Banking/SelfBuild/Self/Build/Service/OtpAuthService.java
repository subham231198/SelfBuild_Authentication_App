package com.Banking.SelfBuild.Self.Build.Service;


import com.Banking.SelfBuild.Self.Build.Entity.OTPEntity;
import com.Banking.SelfBuild.Self.Build.Entity.UsernameAuthEntity;
import com.Banking.SelfBuild.Self.Build.Exceptions.CustomerNotFoundException;
import com.Banking.SelfBuild.Self.Build.Exceptions.SessionNotCreatedException;
import com.Banking.SelfBuild.Self.Build.POJO.SessionManager;
import com.Banking.SelfBuild.Self.Build.Repository.OtpAuthRepo;
import com.Banking.SelfBuild.Self.Build.Repository.UsernameAuthRepo;
import com.Banking.SelfBuild.Self.Build.Utility.OtpGenerator;
import org.apache.catalina.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.*;

@Service
@Transactional(noRollbackFor = {
        ResponseStatusException.class,
        CustomerNotFoundException.class
})
public class OtpAuthService
{
    private static final Logger logger = LoggerFactory.getLogger(OtpAuthService.class);

    @Autowired
    private OtpAuthRepo otpAuthRepo;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private UsernameAuthRepo usernameAuthRepo;


    private OtpGenerator otpGenerator = new OtpGenerator();


    public Map<String, Object> getOTP(String username, String channel) {
        OTPEntity otpEntity = otpAuthRepo.findByCustomerId(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Invalid customerId"));

        String serialToken = otpEntity.getTokenSerial();
        if (serialToken == null || serialToken.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot generate OTP since profile is not CAM40 level"
            );
        }

        Instant expiry = Instant.parse(otpEntity.getExpiryTimeStamp());

        // If OTP expired → regenerate
        if (Instant.now().isAfter(expiry)) {
            String newOtp = otpGenerator.otpGenerator();
            if (newOtp == null || newOtp.isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Unable to generate OTP"
                );
            }

            otpEntity.setSecureCode(newOtp);
            otpEntity.setIssuedTimeStamp(Instant.now().toString());
            if(channel.equalsIgnoreCase("WEB"))
            {
                otpEntity.setExpiryTimeStamp(Instant.now().plusSeconds(30).toString());
            }
            else if(channel.equalsIgnoreCase("MOBILE"))
            {
                otpEntity.setExpiryTimeStamp(Instant.now().plusSeconds(5).toString());
            }
            else
            {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid channel value!");
            }
            otpEntity.setExpiryTimeStamp(Instant.now().plusSeconds(10).toString());
            otpEntity.setOtpUsed(false);
            otpAuthRepo.save(otpEntity);

            logger.info("{ OTP = "+newOtp+", issuedAt = "+otpEntity.getIssuedTimeStamp()+"}");

            Map<String, Object> result = new HashMap<>();
            result.put("otp", newOtp);
            result.put("issuedAt", Instant.now().toString());

            return result;
        }

        logger.info("{ OTP = "+otpEntity.getSecureCode()+", issuedAt = "+otpEntity.getIssuedTimeStamp()+"}");

        Map<String, Object> result = new HashMap<>();
        result.put("otp", otpEntity.getSecureCode());
        result.put("issuedAt", otpEntity.getIssuedTimeStamp());

        return result;
    }

    public Map<String, Object> addDPCloudProfile(String customerId)
    {
        Optional<OTPEntity> existingProfile = otpAuthRepo.findByCustomerId(customerId);
        if(existingProfile.isPresent())
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "An active OTP profile already exists for customerId = "+customerId);
        }
        else
        {
            int serial = 900000000 + new Random().nextInt(999999999);
            String tokenSerial = (String.valueOf(serial));
            Optional<OTPEntity> existingSerial = otpAuthRepo.findByTokenSerial(tokenSerial);
            while (existingSerial.isPresent())
            {
                serial = 900000000 + new Random().nextInt(999999999);
                tokenSerial = (String.valueOf(serial));
                existingSerial = otpAuthRepo.findByTokenSerial(tokenSerial);
            }
            String secureCode = otpGenerator.otpGenerator();
            OTPEntity otpEntity = new OTPEntity();
            otpEntity.setCustomerId(customerId);
            otpEntity.setTokenSerial(tokenSerial);
            otpEntity.setSecureCode(secureCode);
            otpEntity.setIssuedTimeStamp(Instant.now().toString());
            otpEntity.setExpiryTimeStamp(Instant.now().plusSeconds(10).toString());
            otpEntity.setOtpUsed(false);

            Optional<UsernameAuthEntity> entity = usernameAuthRepo.findByCustomerId(customerId);
            if(entity.isEmpty())
            {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid customerId");
            }
            UsernameAuthEntity usernameAuth = entity.get();
            if(usernameAuth.getAuthLevel()<40)
            {
                usernameAuth.setAuthLevel(40);
            }
            usernameAuthRepo.save(usernameAuth);

            otpAuthRepo.save(otpEntity);
            logger.info("Generated OTP for customerId "+customerId+" is "+secureCode);
            Map<String, Object> result = new HashMap<>();
            result.put("message", "OTP profile successfully created for customerId = "+customerId);
            result.put("otp", secureCode);
            result.put("issuedAt", Instant.now().toString());
            return result;
        }
    }

    public Map<String, Object> removeDPCloudProfile(String customerId)
    {
        Optional<OTPEntity> existingProfile = otpAuthRepo.findByCustomerId(customerId);
        if(existingProfile.isPresent())
        {
            OTPEntity otpEntity = existingProfile.get();
            otpAuthRepo.delete(otpEntity);
            Optional<UsernameAuthEntity> entity = usernameAuthRepo.findByCustomerId(customerId);
            if(entity.isEmpty())
            {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid customerId");
            }
            UsernameAuthEntity usernameAuth = entity.get();
            if(usernameAuth.getAuthLevel()==40)
            {
                usernameAuth.setAuthLevel(30);
            }
            usernameAuthRepo.save(usernameAuth);
            return Map.of("message", "OTP profile successfully removed for customerId = "+customerId);
        }
        else
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No OTP profile found for customerId = "+customerId);
        }

    }

    private void handleFailedOtp(UsernameAuthEntity user, int failedAttempt) {

        if (failedAttempt < 2) {
            user.setInvalid_login_attempt_counter(failedAttempt + 1);
            usernameAuthRepo.save(user);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Otp!");
        }

        user.setIsProfileLocked(1);
        user.setProfileSuspended(0);
        usernameAuthRepo.save(user);
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Profile Locked!");
    }


    public String validate_OTP(String customerId, String otp, String correlationId) {

        if (customerId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CustomerId cannot be null");
        }

        UsernameAuthEntity user = usernameAuthRepo.findByCustomerId(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Profile not found!"));

        if (user.getProfileSuspended() == 1) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Profile suspended!");
        }
        if (user.getIsProfileLocked() == 1) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Profile locked!");
        }
        if (user.getAuthLevel() != 40) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Profile auth level is not CAM40!");
        }

        OTPEntity otpEntity = otpAuthRepo.findByCustomerId(customerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid customerId"));

        Integer failedAttempt = user.getInvalid_login_attempt_counter() == null
                ? 0
                : user.getInvalid_login_attempt_counter();

        Instant expiryTime = Instant.parse(otpEntity.getExpiryTimeStamp());

        if (Instant.now().isAfter(expiryTime)) {
            handleFailedOtp(user, failedAttempt);
        }

        if (otpEntity.getOtpUsed()) {
            handleFailedOtp(user, failedAttempt);
        }

        if (!otpEntity.getSecureCode().equals(otp)) {
            handleFailedOtp(user, failedAttempt);
        }

        otpEntity.setOtpUsed(true);
        otpAuthRepo.save(otpEntity);


        sessionManager.getList().removeIf(
                session -> customerId.equals(session.get("customerId"))
        );

        String dspSession = "sf_.*" + UUID.randomUUID() + "==";

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("customerId", customerId);
        map.put("dspSession", dspSession);
        map.put("issuedAt", Instant.now());
        map.put("expiry", Instant.now().plusSeconds(300));
        map.put("session_correlationId", correlationId);

        sessionManager.getList().add(map);

        user.setInvalid_login_attempt_counter(0);
        user.setLast_login_timeStamp(Instant.now().toString());
        usernameAuthRepo.save(user);

        return dspSession;
    }

}
