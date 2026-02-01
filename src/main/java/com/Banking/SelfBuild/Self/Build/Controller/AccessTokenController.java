package com.Banking.SelfBuild.Self.Build.Controller;

import com.Banking.SelfBuild.Self.Build.Service.OauthAccessTokenService;
import com.Banking.SelfBuild.Self.Build.Utility.ConfigReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("dsp/oauth")
public class AccessTokenController {

    @Autowired
    private OauthAccessTokenService oauthAccessTokenService;

    @PostMapping(
            value = "/access_token",
            consumes = "application/x-www-form-urlencoded"
    )
    public ResponseEntity<Map<String, Object>> accessToken(
            @RequestHeader(name = "Authorization") String authorization,
            @RequestParam(name = "auth_code") String authCode,
            @RequestParam("grant_type") String grantType,
            @RequestParam("response_type") String responseType
    ) {

        if (!"token".equalsIgnoreCase(grantType)
                || !"code".equalsIgnoreCase(responseType)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "invalid_grant"
            );
        }

        if (!responseType.equals("code")) {
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("error", "invalid response_type provided!");
            response.put("grant_type", "invalid_response");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        String clientId = "";
        String clientSecret = "";

        try {
            if (authorization == null || !authorization.startsWith("Basic ")) {
                throw new ArrayIndexOutOfBoundsException();
            }

            String base64Credentials = authorization.substring("Basic ".length()).trim();
            byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(decodedBytes, StandardCharsets.UTF_8);

            String[] values = credentials.split(":", 2);
            clientId = values[0];
            clientSecret = values[1];

            if (!clientId.equals(ConfigReader.getAuthCredentials("clientId_oauth"))
                    || !clientSecret.equals(ConfigReader.getAuthCredentials("clientSecret_oauth"))) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied!");
            }

        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Access Denied!");
        }

        return oauthAccessTokenService.generateAccessToken(authCode, clientId);
    }

    @PostMapping(
            value = "v1/session_from_access_token",
            consumes = "application/x-www-form-urlencoded"
    )
    public ResponseEntity<Map<String, Object>> getDspSessionFromAccessToken(@RequestHeader(value = "Authorization") String authorization, @RequestParam(value = "access_token") String authCode) {
        String clientId = "";
        String clientSecret = "";

        try {
            if (authorization == null || !authorization.startsWith("Basic ")) {
                throw new ArrayIndexOutOfBoundsException();
            }

            String base64Credentials = authorization.substring("Basic ".length()).trim();
            byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(decodedBytes, StandardCharsets.UTF_8);

            String[] values = credentials.split(":", 2);
            clientId = values[0];
            clientSecret = values[1];

            if (!clientId.equals(ConfigReader.getAuthCredentials("clientId_oauth"))
                    || !clientSecret.equals(ConfigReader.getAuthCredentials("clientSecret_oauth"))) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied!");
            }

        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Access Denied!");
        }
        return new ResponseEntity<>(oauthAccessTokenService.getSessionFromAccessToken(authCode), HttpStatus.OK);
    }
}
