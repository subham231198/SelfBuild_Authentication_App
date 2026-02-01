package com.Banking.SelfBuild.Self.Build.Service;

import com.Banking.SelfBuild.Self.Build.POJO.ACCESS_TOKEN_STORAGE;
import com.Banking.SelfBuild.Self.Build.POJO.AUTH_CODE_STORAGE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class OauthAccessTokenService {

    private static final long ACCESS_TOKEN_TTL_SECONDS = 300L;

    @Autowired
    private AUTH_CODE_STORAGE authCodeStorage;

    @Autowired
    private ACCESS_TOKEN_STORAGE accessTokenStorage;

    public ResponseEntity<Map<String, Object>> generateAccessToken(String authCode, String clientId) {
        if (authCode == null || clientId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing authorization code or client ID.");
        }

        List<Map<String, Object>> authStore = authCodeStorage == null ? null : authCodeStorage.getAuthCode_storage();
        if (authStore == null || authStore.isEmpty()) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("error", "the provided token is either invalid, expired or revoked");
            error.put("grant_type", "invalid_grant");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        Optional<Map<String, Object>> optEntry = authStore.stream()
                .filter(entry -> authCode.equals(String.valueOf(entry.get("authCode")))
                        && clientId.equals(String.valueOf(entry.get("clientId"))))
                .findFirst();

        if (!optEntry.isPresent()) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("error", "the provided token is either invalid, expired or revoked");
            error.put("grant_type", "invalid_grant");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        Map<String, Object> authCodeEntry = optEntry.get();

        Object expiryObj = authCodeEntry.get("expiry");
        Instant expiry;
        try {
            if (expiryObj instanceof Instant) {
                expiry = (Instant) expiryObj;
            } else if (expiryObj instanceof String) {
                expiry = Instant.parse((String) expiryObj);
            } else {
                // unknown expiry representation -> treat as invalid
                removeFromListSafely(authStore, authCodeEntry);
                Map<String, Object> error = new LinkedHashMap<>();
                error.put("error", "the provided token is either invalid, expired or revoked");
                error.put("grant_type", "invalid_grant");
                return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            removeFromListSafely(authStore, authCodeEntry);
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("error", "the provided token is either invalid, expired or revoked");
            error.put("grant_type", "invalid_grant");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        if (Instant.now().isAfter(expiry)) {
            removeFromListSafely(authStore, authCodeEntry);
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("error", "the provided token is either invalid, expired or revoked");
            error.put("grant_type", "invalid_grant");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        String tokenId = String.valueOf(authCodeEntry.get("csrfToken"));
        String customerId = String.valueOf(authCodeEntry.get("customerId"));

        // remove auth code entry
        removeFromListSafely(authStore, authCodeEntry);

        String accessToken = UUID.randomUUID().toString().replace("-", "");
        Instant issued = Instant.now();
        Instant expiresAt = issued.plusSeconds(ACCESS_TOKEN_TTL_SECONDS);

        Map<String, Object> tokenEntry = new LinkedHashMap<>();
        tokenEntry.put("access_token", accessToken);
        tokenEntry.put("customerId", customerId);
        tokenEntry.put("issued_at", issued);
        tokenEntry.put("expiry", expiresAt);
        tokenEntry.put("csrfToken", tokenId);

        List<Map<String, Object>> accessStore = accessTokenStorage == null ? null : accessTokenStorage.getAccessToken_storage();
        if (accessStore == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Access token storage not available.");
        }
        addToListSafely(accessStore, tokenEntry);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("access_token", accessToken);
        response.put("token_type", "Bearer");
        response.put("issued_at", issued.toString());
        response.put("expiry", expiresAt.toString());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private void removeFromListSafely(List<Map<String, Object>> list, Map<String, Object> entry) {
        if (list == null) return;
        synchronized (list) {
            list.remove(entry);
        }
    }

    private void addToListSafely(List<Map<String, Object>> list, Map<String, Object> entry) {
        if (list == null) return;
        synchronized (list) {
            list.add(entry);
        }
    }


    public Map<String, Object> getSessionFromAccessToken(String accessToken) {
        List<Map<String, Object>> accessStore = accessTokenStorage == null ? null : accessTokenStorage.getAccessToken_storage();
        if (accessStore == null || accessStore.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied!");
        }

        Optional<Map<String, Object>> optEntry = accessStore.stream()
                .filter(entry -> accessToken.equals(String.valueOf(entry.get("access_token"))))
                .findFirst();

        if (!optEntry.isPresent()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied!");
        }

        Map<String, Object> tokenEntry = optEntry.get();

        Object expiryObj = tokenEntry.get("expiry");
        Instant expiry;
        try {
            if (expiryObj instanceof Instant) {
                expiry = (Instant) expiryObj;
            } else if (expiryObj instanceof String) {
                expiry = Instant.parse((String) expiryObj);
            } else {
                // unknown expiry representation -> remove and deny
                removeFromListSafely(accessStore, tokenEntry);
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied!");
            }
        } catch (Exception ex) {
            removeFromListSafely(accessStore, tokenEntry);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied!");
        }

        if (Instant.now().isAfter(expiry)) {
            removeFromListSafely(accessStore, tokenEntry);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied!");
        }

        String dspSession = String.valueOf(tokenEntry.get("csrfToken"));
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("tokenId", dspSession);
        response.put("role", "./dsp");
        return response;
    }

}
