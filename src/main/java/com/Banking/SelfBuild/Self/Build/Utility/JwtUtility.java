package com.Banking.SelfBuild.Self.Build.Utility;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtility {

    private static final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public static String generateCustomerToken(String sessionId, String timeStamp, String expiry, String customerId, String customerType) {

        Map<String, Object> headers = new HashMap<>();
        headers.put("tokenType", "JWT");
        headers.put("timestamp", timeStamp);
        headers.put("expiry", expiry);


        Map<String, Object> claims = new HashMap<>();
        claims.put("customerId", customerId);
        claims.put("customer_type", customerType);
        claims.put("sessionId", sessionId);

        return Jwts.builder()
                .setHeader(headers)
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plusSeconds(3600)))
                .signWith(secretKey)
                .compact();
    }
}