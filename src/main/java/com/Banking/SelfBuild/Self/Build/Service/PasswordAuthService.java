package com.Banking.SelfBuild.Self.Build.Service;

import com.Banking.SelfBuild.Self.Build.Entity.PasswordAuthEntity;
import com.Banking.SelfBuild.Self.Build.Entity.UsernameAuthEntity;
import com.Banking.SelfBuild.Self.Build.Exceptions.CustomerNotFoundException;
import com.Banking.SelfBuild.Self.Build.Exceptions.SessionNotCreatedException;
import com.Banking.SelfBuild.Self.Build.POJO.SessionManager;
import com.Banking.SelfBuild.Self.Build.Repository.PasswordAuthRepo;
import com.Banking.SelfBuild.Self.Build.Repository.UsernameAuthRepo;
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
public class PasswordAuthService
{
    private static final Logger logger = LoggerFactory.getLogger(PasswordAuthService.class);

    @Autowired
    private PasswordAuthRepo passwordAuthRepo;

    @Autowired
    private UsernameAuthRepo usernameAuthRepo;

    @Autowired
    private SessionManager sessionManager;

    public Boolean validatePassword(String customerId, String password)
    {
        String encodedPassword = Base64.getEncoder().encodeToString(password.getBytes());
        Optional<PasswordAuthEntity> customer = passwordAuthRepo.findByCustomerIdAndPassword(customerId, encodedPassword);
        if(customer.isPresent())
        {
            return true;
        }
        return false;
    }

    public String authenticate(String custId, String pwd, String correlationId)
    {
        Boolean result = validatePassword(custId, pwd);
        Optional<UsernameAuthEntity> usernameAuthEntity = usernameAuthRepo.findByCustomerId(custId);
        UsernameAuthEntity entity = usernameAuthEntity.get();
        Assert.isTrue(usernameAuthEntity.isPresent(), "Customer details not found!");
        Integer failedCounterAttempt = usernameAuthEntity.get().getInvalid_login_attempt_counter();
        if(result)
        {
            List<Map<String, Object>> duplicateSession = sessionManager.getList();
            for(int i = 0; i < duplicateSession.size(); i++)
            {
                if(duplicateSession.get(i).containsValue(custId))
                {
                    Instant expired = (Instant) duplicateSession.get(i).get("expiry");
                    if(Instant.now().isAfter(expired)) {
                        Iterator<Map<String, Object>> iterator = duplicateSession.iterator();

                        while (iterator.hasNext()) {
                            Map<String, Object> session = iterator.next();
                            if (session.equals(session.get("dspSession"))) {
                                iterator.remove();
                            }
                        }
                    }
                    else
                    {
                        List<Map<String, Object>> sessions = sessionManager.getList();
                        sessions.removeIf(session ->
                                custId.equals(session.get("customerId"))
                        );
                    }
                }
            }
            String dspSession = "sf_.*"+UUID.randomUUID().toString()+"==";
            if (!dspSession.isEmpty())
            {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("customerId", custId);
                map.put("dspSession", dspSession);
                map.put("issuedAt", Instant.now());
                map.put("expiry", Instant.now().plusSeconds(300));
                map.put("session_correlationId", correlationId);

                usernameAuthEntity.get().setLast_login_timeStamp(Instant.now().toString());
                usernameAuthEntity.get().setInvalid_login_attempt_counter(0);
                sessionManager.getList().add(map);
                return dspSession;
            }
            else
            {
                throw new SessionNotCreatedException("Unable to create dspSession");
            }
        }
        else
        {
            if(failedCounterAttempt == 0 || failedCounterAttempt < 2)
            {
                usernameAuthEntity.get().setInvalid_login_attempt_counter(failedCounterAttempt + 1);
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials!");
            }
            else if (failedCounterAttempt == 2)
            {
                if(entity.getIsProfileLocked() != null && entity.getIsProfileLocked()==0)
                {
                    entity.setIsProfileLocked(1);
                }
                if(entity.getProfileSuspended() != null && entity.getProfileSuspended() == 0)
                {
                    entity.setProfileSuspended(0);
                }
                else if(entity.getProfileSuspended() != null && entity.getProfileSuspended() == 1)
                {
                    entity.setProfileSuspended(1);
                }
                usernameAuthRepo.save(entity);
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Profile Locked!");
            }
        }
        return null;
    }

    public void unlockProfile(String customerId) {
        Optional<UsernameAuthEntity> usernameAuthEntityOptional = usernameAuthRepo.findByCustomerId(customerId);
        if (usernameAuthEntityOptional.isPresent()) {
            UsernameAuthEntity entity = usernameAuthEntityOptional.get();
            entity.setIsProfileLocked(0);
            entity.setInvalid_login_attempt_counter(0);
            entity.setLast_login_timeStamp("11-11-1111T00:00:00Z");
            usernameAuthRepo.save(entity);
            logger.info("Profile unlocked for customer: {}", customerId);
        } else {
            throw new CustomerNotFoundException("Customer not found: " + customerId);
        }
    }

}
