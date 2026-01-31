package com.Banking.SelfBuild.Self.Build.Controller;

import com.Banking.SelfBuild.Self.Build.POJO.SessionAttributes;
import com.Banking.SelfBuild.Self.Build.Service.SessionAttributesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping(value = "/Identity")
public class SessionAttributesController
{
    @Autowired
    private SessionAttributesService sessionAttributesService;

    @PostMapping(value = "/indBank/v1/dsp/sessions")
    private ResponseEntity<Map<String, Object>> getSessionAttribute(@RequestParam(value = "_action") String action, @RequestHeader(name = "x-channel")  String channel, @RequestHeader(name = "x-group-member") String groupMember, @RequestBody SessionAttributes sessionAttributes)
    {
        if(sessionAttributes.getTokenId()==null || sessionAttributes.getTokenId().isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "tokenId cannot be null or empty!");
        }
        if(!channel.equalsIgnoreCase("WEB") && !channel.equalsIgnoreCase("MOBILE"))
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid header value x-channel!");
        }
        if(!groupMember.equalsIgnoreCase("INDBANK"))
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid header value x-group-member!");
        }
        if(action==null || action.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "_action parameter cannot be null or empty!");
        }
        if(!action.equalsIgnoreCase("getSessionInfo") && !action.equalsIgnoreCase("getAllSessionInfo"))
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid _action parameter value provided!");
        }
        Map<String, Object> result = sessionAttributesService.getSessionInfo(sessionAttributes.getTokenId());
        if(result!=null)
        {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        else
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
