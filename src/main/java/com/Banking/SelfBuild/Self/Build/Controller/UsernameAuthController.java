package com.Banking.SelfBuild.Self.Build.Controller;

import com.Banking.SelfBuild.Self.Build.POJO.UsernameAuthRequest;
import com.Banking.SelfBuild.Self.Build.Service.UsernameAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping(value = "/Identity")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class UsernameAuthController
{
    private static final Logger logger = LoggerFactory.getLogger(UsernameAuthController.class);
    @Autowired
    private UsernameAuthService usernameAuthService;

    @PostMapping(value = "/indBank/getCustomerAuthInfo")
    public ResponseEntity<Map<String, Object>> getCustomerAuthInfo(@RequestParam(name = "_service") String service,
                                                                   @RequestHeader(name = "x-channel") String channel,
                                                                   @RequestHeader(name = "x-group-member") String grpMember,
                                                                   @RequestBody UsernameAuthRequest usernameAuthRequest)
    {
        if(!service.equals("usernameAuthService"))
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid service name provided in query param, "+service);
        }
        if(!channel.equals("WEB") && !channel.equals("MOBILE"))
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid header value provided against x-channel");
        }
        if(!grpMember.equals("INDBANK"))
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid header value provided against x-group-member");
        }
        if(usernameAuthRequest.getCallbacksInput()==null)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Input callback state cannot be null");
        }
        if(usernameAuthRequest.getCallbacksValue()==null)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "callback value cannot be null");
        }
        if(!usernameAuthRequest.getCallbacksInput().getName().equals("customerId"))
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input callback name "+usernameAuthRequest.getCallbacksInput().getName());
        }
        Map<String, Object> result = usernameAuthService.getProfileAuthDetails(usernameAuthRequest.getCallbacksValue().getValue());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
