package com.Banking.SelfBuild.Self.Build.Service;

import com.Banking.SelfBuild.Self.Build.POJO.AUTH_CODE_STORAGE;
import com.Banking.SelfBuild.Self.Build.POJO.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OAuthAuthorizationService {

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private AUTH_CODE_STORAGE authCodeStorage;

    public Boolean validateCsrfToken(String tokenId) {
        if (tokenId == null) {
            return false;
        }

        List<Map<String, Object>> sessions = sessionManager.getList();
        if (sessions == null || sessions.isEmpty()) {
            return false;
        }

        Map<String, Object> session = sessions.stream()
                .filter(s -> tokenId.equals(String.valueOf(s.get("dspSession"))))
                .findFirst()
                .orElse(null);

        if (session == null) {
            return false;
        }

        Object expiryObj = session.get("expiry");
        Instant expiry;
        try {
            if (expiryObj instanceof Instant) {
                expiry = (Instant) expiryObj;
            } else if (expiryObj instanceof String) {
                expiry = Instant.parse((String) expiryObj);
            } else {
                // unknown representation -> remove and reject
                sessions.removeIf(s -> tokenId.equals(String.valueOf(s.get("dspSession"))));
                return false;
            }
        } catch (Exception ex) {
            sessions.removeIf(s -> tokenId.equals(String.valueOf(s.get("dspSession"))));
            return false;
        }

        if (Instant.now().isAfter(expiry)) {
            sessions.removeIf(s -> tokenId.equals(String.valueOf(s.get("dspSession"))));
            return false;
        }

        return true;
    }

    public String generateAuthCode(String clientId, String csrfToken) {
        if (clientId == null || csrfToken == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing clientId or csrfToken");
        }

        List<Map<String, Object>> sessions = sessionManager.getList();
        if (sessions == null || sessions.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session not found");
        }

        Map<String, Object> session = sessions.stream()
                .filter(s -> csrfToken.equals(String.valueOf(s.get("dspSession"))))
                .findFirst()
                .orElse(null);

        if (session == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid csrf token");
        }

        String customerId = String.valueOf(session.get("customerId"));
        if (customerId == null || customerId.isEmpty() || "null".equals(customerId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid session customerId");
        }

        if (authCodeStorage == null || authCodeStorage.getAuthCode_storage() == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Auth code storage not available");
        }


        synchronized (authCodeStorage.getAuthCode_storage()) {
            authCodeStorage.getAuthCode_storage().removeIf(entry ->
                    clientId.equals(String.valueOf(entry.get("clientId")))
                            && customerId.equals(String.valueOf(entry.get("customerId")))
            );

            String authCode = UUID.randomUUID().toString().replace("-", "");
            Instant expiry = Instant.now().plusSeconds(10);

            Map<String, Object> authCodeEntry = new ConcurrentHashMap<>();
            authCodeEntry.put("clientId", clientId);
            authCodeEntry.put("csrfToken", csrfToken);
            authCodeEntry.put("customerId", customerId);
            authCodeEntry.put("authCode", authCode);
            authCodeEntry.put("expiry", expiry);

            authCodeStorage.getAuthCode_storage().add(authCodeEntry);

            return authCode;
        }
    }
}
