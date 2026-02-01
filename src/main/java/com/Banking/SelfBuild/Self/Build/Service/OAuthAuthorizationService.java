package com.Banking.SelfBuild.Self.Build.Service;

import com.Banking.SelfBuild.Self.Build.POJO.AUTH_CODE_STORAGE;
import com.Banking.SelfBuild.Self.Build.POJO.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OAuthAuthorizationService
{
    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private AUTH_CODE_STORAGE authCodeStorage;

    private static final Map<String, Object> AUTH_CODE_STORE =
            new ConcurrentHashMap<>();

    public Boolean validateCsrfToken(String tokenId) {
        Map<String, Object> session = sessionManager.getList().stream()
                .filter(s->s.get("dspSession").equals(tokenId))
                .findFirst()
                .orElse(null);

        if(session == null)
        {
            return false;
        }
        Instant expiry = (Instant) session.get("expiry");
        if(Instant.now().isAfter(expiry))
        {
            sessionManager.getList().removeIf(s -> tokenId.equals(s.get("dspSession")));
            return false;
        }
        return true;
    }


    public String generateAuthCode(String clientId, String csrfToken) {
        Map<String, Object> session = sessionManager.getList().stream()
                .filter(s -> csrfToken.equals(s.get("dspSession")))
                .findFirst()
                .orElse(null);

        String customerId = (String) session.get("customerId");
//        authCodeStorage.getAuthCode_storage().removeIf(
//                entry -> clientId.equals(entry.get("clientId"))
//        );

        Map<String, Object> existingEntry = authCodeStorage.getAuthCode_storage().stream()
                .filter(entry -> clientId.equals(entry.get("clientId"))
                        && customerId.equals(entry.get("customerId")))
                .findFirst()
                .orElse(null);

        if(existingEntry != null)
        {
            authCodeStorage.getAuthCode_storage().remove(existingEntry);
        }

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
