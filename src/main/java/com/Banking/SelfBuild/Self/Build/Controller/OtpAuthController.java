package com.Banking.SelfBuild.Self.Build.Controller;

import com.Banking.SelfBuild.Self.Build.POJO.OtpAuthRequest;
import com.Banking.SelfBuild.Self.Build.POJO.SessionAttributes;
import com.Banking.SelfBuild.Self.Build.Service.OtpAuthService;
import com.Banking.SelfBuild.Self.Build.Service.PasswordAuthService;
import com.Banking.SelfBuild.Self.Build.Utility.ConfigReader;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class OtpAuthController
{
    private static final Logger logger = LoggerFactory.getLogger(OtpAuthController.class);
    @Autowired
    private OtpAuthService otpAuthService;

    @Autowired
    PasswordAuthService passwordAuthService;

    @GetMapping(value = "testingservices/getOtp")
    public ResponseEntity<Map<String, Object>> otpGenerator(@RequestHeader(name = "x-channel") String channel, @RequestParam(name = "customerId") String customerId)
    {
        if(channel.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "x-channel header cannot be null or empty!");
        }
        if(!channel.equalsIgnoreCase("WEB") && !channel.equalsIgnoreCase("MOBILE"))
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid header value provided against x-channel!");
        }
        if(customerId.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CustomerId cannot be null or blank!");
        }

        logger.info("Incoming request: OTP for customerId = "+customerId);
        return new ResponseEntity<>(otpAuthService.getOTP(customerId, channel), HttpStatus.OK);
    }

    @PostMapping(value = "/Identity/indBank/auth")
    public ResponseEntity<Map<String, Object>>  otpAuthController(@RequestHeader(name = "x-channel") @NonNull String channel,
                                                                  @RequestHeader(name = "x-group-member") @NonNull String groupMember,
                                                                  @RequestHeader(name = "x-correlationId") @NonNull String correlationId,
                                                                  @RequestHeader(name = "x-country") @NonNull String country,
                                                                  @RequestHeader(name = "Authorization") @NonNull String authorization,
                                                                  @RequestParam(name = "_service") String service,
                                                                  @RequestBody @NonNull OtpAuthRequest otpAuthRequest,
                                                                  HttpServletResponse response)
    {
        if(authorization.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Authorization header cannot be null or empty!");
        }
        if (!authorization.startsWith("Basic "))
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid Authorization header value provided!");
        }
        String base64Credentials = authorization.substring("Basic ".length()).trim();
        byte[] decodedBytes = java.util.Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(decodedBytes);
        String[] values = credentials.split(":", 2);
        String username = values[0];
        String password = values[1];
        if(!username.equals(ConfigReader.getAuthCredentials("clientId")) || !password.equals(ConfigReader.getAuthCredentials("clientSecret")))
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password in Authorization header!");
        }
        if(channel.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "x-channel header cannot be null or empty!");
        }
        if(!channel.equalsIgnoreCase("WEB") && !channel.equalsIgnoreCase("MOBILE"))
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid header value provided against x-channel!");
        }
        if(groupMember.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "x-group-member header cannot be null or empty!");
        }
        if(!groupMember.equalsIgnoreCase("INDBANK"))
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid header value provided against x-group-member!");
        }
        if(country.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "x-country header cannot be null or empty!");
        }
        if(!country.equalsIgnoreCase("INDIA"))
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid header value provided against x-country!");
        }
        if(correlationId.isEmpty())
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "x-correlationId header cannot be null or empty!");
        }
        Pattern p = Pattern.compile("^[A-Za-z0-9]+$");
        Matcher m = p.matcher(correlationId);
        if(!m.matches())
        {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "x-correlationId should be alpha-numeric");
        }
        String value1 = otpAuthRequest.getCallbacksValue_customerId().getValue();
        String value2 = otpAuthRequest.getCallbacksValue_accessKeyValue().getValue();
        if(service.equals("OtpAuthService"))
        {
            String result = otpAuthService.validate_OTP(value1, value2, correlationId);
            if(result != null)
            {
                Cookie cookie = new Cookie(
                        "dspSession", result
                );

                cookie.setHttpOnly(true);
                cookie.setSecure(true);
                cookie.setPath("/");
                cookie.setMaxAge(300);
                response.addCookie(cookie);
                Map<String, Object> token_details = new LinkedHashMap<>();
                token_details.put("tokenId", result);
                token_details.put("role", "./dsp");
                return new ResponseEntity<>(token_details, HttpStatus.OK);
            }
            else
            {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED ,"Invalid Otp!");
            }
        }
        else if(service.equals("PasswordAuthService"))
        {
            String result = passwordAuthService.authenticate(value1, value2, correlationId);
            if(!result.isEmpty() && result.contains("sf_.*"))
            {
                Cookie cookie = new Cookie(
                        "dspSession", result
                );

                cookie.setHttpOnly(true);
                cookie.setSecure(true);
                cookie.setPath("/");
                cookie.setMaxAge(300);
                response.addCookie(cookie);
                Map<String, Object> token_details = new LinkedHashMap<>();
                token_details.put("tokenId", result);
                token_details.put("role", "./dsp");
                return new ResponseEntity<>(token_details, HttpStatus.OK);
            }
            else
            {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        }
        else
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid service name provided!");
        }
    }

    @PostMapping("/testingServices/addDPCloudProfile")
    public ResponseEntity<Map<String, Object>> addDPCloudProfile(
            @RequestBody SessionAttributes sessionAttributes) {

        String tokenId = sessionAttributes.getTokenId();
        if (tokenId == null || tokenId.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "tokenId cannot be null or empty!");
        }

        ResponseEntity<Map<String, Object>> response =
                sessionAttributesInfo(tokenId);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve session information");
        }

        Map<String, Object> responseBody = response.getBody();

        if (Boolean.FALSE.equals(responseBody.get("valid"))) {
            return ResponseEntity.ok(Map.of("valid", false));
        }

        if (Boolean.TRUE.equals(responseBody.get("isSessionValid"))) {
            String customerId = responseBody.get("customerId").toString();
            logger.info("CustomerId for adding DPCloudProfile: " + customerId);
            Map<String, Object> result = otpAuthService.addDPCloudProfile(customerId);
            if (result != null)
            {
                return ResponseEntity.ok(result);
            }
            else
            {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Unexpected session response"));
    }


    public ResponseEntity<Map<String, Object>> sessionAttributesInfo(String tokenId) {

        RestTemplate restTemplate = new RestTemplate();

        String url = ConfigReader.getHost("SelfBuild_host", "DomainHosts")
                + ConfigReader.getURL("getSessionInfo", "SelfBuild");

        URI uri = UriComponentsBuilder
                .fromHttpUrl(url)
                .queryParam("_action", "getSessionInfo")
                .build()
                .encode()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.add("x-channel", "MOBILE");
        headers.add("x-group-member", "INDBANK");
        headers.add("dspSession", tokenId);

        Map<String, String> requestBody = Map.of("tokenId", tokenId);

        HttpEntity<Map<String, String>> entity =
                new HttpEntity<>(requestBody, headers);

        return restTemplate.exchange(
                uri,
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );
    }

    @PostMapping(value = "/testingServices/removeDPCloudProfile")
    public ResponseEntity<Map<String, Object>> removeDPCloudProfile(@RequestBody SessionAttributes sessionAttributes) {
        String tokenId = sessionAttributes.getTokenId();
        if (tokenId == null || tokenId.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "tokenId cannot be null or empty!");
        }

        ResponseEntity<Map<String, Object>> response =
                sessionAttributesInfo(tokenId);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve session information");
        }

        Map<String, Object> responseBody = response.getBody();

        if (Boolean.FALSE.equals(responseBody.get("valid"))) {
            return ResponseEntity.ok(Map.of("valid", false));
        }

        if (Boolean.TRUE.equals(responseBody.get("isSessionValid"))) {
            String customerId = responseBody.get("customerId").toString();
            logger.info("CustomerId for adding DPCloudProfile: " + customerId);
            Map<String, Object> result = otpAuthService.removeDPCloudProfile(customerId);
            if (result != null)
            {
                return ResponseEntity.ok(result);
            }
            else
            {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Unexpected session response"));
    }
}
