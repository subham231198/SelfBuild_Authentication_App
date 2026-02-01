package com.Banking.SelfBuild.Self.Build.Controller;


import com.Banking.SelfBuild.Self.Build.Service.OAuthAuthorizationService;
import com.Banking.SelfBuild.Self.Build.Utility.ConfigReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("dsp/oauth")
public class OauthAuthorizeController {

    @Autowired
    private OAuthAuthorizationService authorizationService;


    @GetMapping(value = "/authorize", consumes = "application/x-www-form-urlencoded", produces = "application/json")
    public ResponseEntity<Map<String, Object>> authorize(
            @RequestParam("client_id") String clientId,
            @RequestParam("grant_type") String grantType,
            @RequestParam("response_type") String responseType,
            @RequestParam("csrf") String csrfToken
    ) {
        if (!"token".equalsIgnoreCase(grantType)
                || !"code".equalsIgnoreCase(responseType)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid OAuth parameters"
            );
        }

        if(!responseType.equals("code"))
        {
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("error", "invalid response_type provided!");
            response.put("grant_type", "unsupported_response_type");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        if(!clientId.equals(ConfigReader.getAuthCredentials("clientId_oauth")))
        {
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("error", "invalid client_id provided!");
            response.put("grant_type", "invalid_grant");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        Boolean isCsrfValid = authorizationService.validateCsrfToken(csrfToken);
        if(!isCsrfValid) {
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("error", "the provided token is either expired or invalid");
            response.put("grant_type", "invalid_grant");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }


        String authCode = authorizationService.generateAuthCode(
                clientId, csrfToken
        );

        URI redirectUri = URI.create(
                "https://www.indbank.com?code=" + authCode
        );

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(redirectUri)
                .build();
    }
}
