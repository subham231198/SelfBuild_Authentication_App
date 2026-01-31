package com.Banking.SelfBuild.Self.Build.Service;

import com.Banking.SelfBuild.Self.Build.Entity.UsernameAuthEntity;
import com.Banking.SelfBuild.Self.Build.Exceptions.CustomerNotFoundException;
import com.Banking.SelfBuild.Self.Build.Exceptions.CustomerProfileSuspendedException;
import com.Banking.SelfBuild.Self.Build.Repository.UsernameAuthRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Service
public class UsernameAuthService
{
    private static final Logger logger = LoggerFactory.getLogger(UsernameAuthService.class);

    @Autowired
    private UsernameAuthRepo usernameAuthRepo;

    public Map<String, Object> getProfileAuthDetails(String customerId)
    {
        Optional<UsernameAuthEntity> user = usernameAuthRepo.findByCustomerId(customerId);
        if(user.isPresent())
        {
            Map<String, Object> userDetails = new HashMap<>();
            userDetails.put("customerId", user.get().getCustomerId());
            if(user.get().getIsProfileLocked()==0)
            {
                userDetails.put("isProfileLocked", false);
            }
            else if(user.get().getIsProfileLocked()==1)
            {
                userDetails.put("isProfileLocked", true);
            }
            if(user.get().getProfileSuspended() == 0)
            {
                userDetails.put("isProfileSuspended", false);
            }
            else
            {
                userDetails.put("isProfileSuspended", true);
            }
            userDetails.put("authLevel", user.get().getAuthLevel());
            userDetails.put("failedLoginAttempts", user.get().getInvalid_login_attempt_counter());

            logger.info(user.toString());

            return userDetails;

        }
        else
        {
            throw new CustomerNotFoundException("Customer not found!");
        }
    }

//    public Map<String, Object> recoverUsername(String username)
//    {
//    }
}
