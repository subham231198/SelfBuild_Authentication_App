package com.Banking.SelfBuild.Self.Build.Service;

import com.Banking.SelfBuild.Self.Build.POJO.SessionManager;
import com.Banking.SelfBuild.Self.Build.Utility.ConfigReader;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Instant;
import java.util.*;

@Service
public class SessionAttributesService
{
    @Autowired
    private SessionManager sessionManager;

    public Map<String, Object> getSessionInfo(String tokenId)
    {
        Map<String, Object> session = sessionManager.getList().stream()
                .filter(s->s.get("dspSession").equals(tokenId))
                .findFirst()
                .orElse(null);

        if(session == null)
        {
            return Map.of("valid", false);
        }

        String customerId = session.get("customerId").toString();
        String sessionCorrelation = session.get("session_correlationId").toString();
        Instant expiry = (Instant) session.get("expiry");

        if(Instant.now().isAfter(expiry))
        {
            sessionManager.getList().removeIf(s -> tokenId.equals(s.get("dspSession")));
            return Map.of("valid", false);
        }
        RestTemplate restTemplate = new RestTemplate();
        String domain = ConfigReader.getHost("OpenIDM_host", "DomainHosts");
        String basePath = ConfigReader.getURL("GetSessionInformation", "OpenIDM");
        String baseUrl = domain+basePath;

        URI uri = UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .queryParam("_username", customerId)
                .build()
                .encode()
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                entity,
                Map.class
        );

        if(response.getStatusCode().is2xxSuccessful())
        {
            Map<String, Object> responseBody = new LinkedHashMap<>();
            responseBody = response.getBody();
            String iss_jwt = responseBody.get("issued_JWT").toString();
            DecodedJWT jwt = JWT.decode(iss_jwt);
            String headerJson = new String(Base64.getUrlDecoder().decode(jwt.getHeader()));
            JSONObject header = new JSONObject(headerJson);
            String alg = header.optString("alg");

            String payloadJson = new String(Base64.getUrlDecoder().decode(jwt.getPayload()));
            JSONObject payload = new JSONObject(payloadJson);
            String first_name = payload.optString("first_name");
            String last_name = payload.optString("last_name");
            String email = payload.optString("email");
            String phone = payload.optString("phone");
            String accountType = payload.optString("accountType");

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("isSessionValid", true);
            result.put("sessionCorrelationId", sessionCorrelation);
            result.put("customerId", session.get("customerId"));
            result.put("tokenId", session.get("dspSession"));
            result.put("first_name", first_name);
            result.put("last_name", last_name);
            result.put("email", email);
            result.put("phone", phone);
            result.put("accountType", accountType);
            result.put("issuedAt", session.get("issuedAt"));
            result.put("expiry", session.get("expiry"));
            result.put("alg", alg);
            return result;
        }
        else
        {
            Map<String, Object> responseBody = new LinkedHashMap<>();
            responseBody = response.getBody();
            Integer code = Integer.parseInt(responseBody.get("code").toString());
            String reason = responseBody.get("reason").toString();
            String message = responseBody.get("message").toString();
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("code", code);
            error.put("reason", reason);
            error.put("message", message);
            return error;
        }

    }
}
