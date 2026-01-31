package com.Banking.SelfBuild.Self.Build.Controller;

import com.Banking.SelfBuild.Self.Build.Service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/Identity")
public class DSPSessionController
{
    @Autowired
    private SessionService sessionService;

    @PostMapping(value = "/indBank/v1/dsp/getsessions")
    public List<Map<String, Object>> get_dspSessions(@RequestParam(value = "_action") String action)
    {
        if(action==null || action.isEmpty() || !action.equalsIgnoreCase("translate"))
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid _action parameter value provided!");
        }
        return sessionService.getAllSessions();
    }
}
