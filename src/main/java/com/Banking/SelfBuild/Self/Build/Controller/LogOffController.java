package com.Banking.SelfBuild.Self.Build.Controller;

import com.Banking.SelfBuild.Self.Build.POJO.LogOffProvider;
import com.Banking.SelfBuild.Self.Build.Service.LogOffProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
public class LogOffController
{

    @Autowired
    private LogOffProviderService logOffProviderService;

    @PostMapping(value = "dsp/rest-sts/sessions-logout")
    public ResponseEntity<Map<String, Object>> session_logout(@RequestParam(name = "_action") String action, @RequestBody LogOffProvider logOffProvider)
    {
        if(!action.equals("translate"))
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid query passed against action!");
        }
        if(!logOffProvider.getInputTokenState().getToken_type().equals("SSOTOKEN"))
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token-type in request");
        }
        else if(logOffProvider.getInputTokenState().getTokenId().isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "tokenId cannot be null or blank!");
        }
        else if(!logOffProvider.getOutputTokenState().getSubject_confirmation().equals("Bearer"))
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid value passed against subject confirmation!");
        }
        else
        {
            Map<String, Object> result = logOffProviderService.logout(logOffProvider.getInputTokenState().getTokenId());
            if(result!=null)
            {
                return new ResponseEntity<>(result, HttpStatus.OK);
            }
            else
            {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        }
    }
}
