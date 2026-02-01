package com.Banking.SelfBuild.Self.Build.Controller;

import com.Banking.SelfBuild.Self.Build.POJO.UsernameAuthRequest;
import com.Banking.SelfBuild.Self.Build.Service.UsernameAuthService;
import com.Banking.SelfBuild.Self.Build.Utility.ConfigReader;
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
                                                                   @RequestHeader(name = "Authorization") String authHeader,
                                                                   @RequestBody UsernameAuthRequest usernameAuthRequest)
    {
        if (authHeader==null || authHeader.isEmpty() || !authHeader.startsWith("Basic "))
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or missing Authorization header");
        }
        if(!service.equals("usernameAuthService"))
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid service name provided in query param, "+service);
        }
        String base64Credentials = authHeader.substring("Basic ".length()).trim();
        byte[] decodedBytes = java.util.Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(decodedBytes);
        String[] values = credentials.split(":", 2);
        String username = values[0];
        String password = values[1];
        if(!username.equals(ConfigReader.getAuthCredentials("clientId")) || !password.equals(ConfigReader.getAuthCredentials("clientSecret")))
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password in Authorization header!");
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
        if (!authHeader.startsWith("Basic "))
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid Authorization header value provided!");
        }
        Map<String, Object> result = usernameAuthService.getProfileAuthDetails(usernameAuthRequest.getCallbacksValue().getValue());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
