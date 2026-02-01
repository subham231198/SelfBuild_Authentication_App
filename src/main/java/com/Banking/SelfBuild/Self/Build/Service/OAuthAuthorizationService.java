package com.Banking.SelfBuild.Self.Build.Service;

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

    public void removeDuplicateAuthCode(String clientId) {
        Map<String, Object> getAUTH_CODE_STORE = AUTH_CODE_STORE;
       if(getAUTH_CODE_STORE.containsKey(clientId))
       {
           getAUTH_CODE_STORE.remove(clientId);
       }
    }

    public String generateAuthCode(String clientId, String csrfToken) {
        Map<String, Object> session = sessionManager.getList().stream()
                .filter(s->s.get("dspSession").equals(csrfToken))
                .findFirst()
                .orElse(null);

        String customerId = (String) session.get("customerId");
        removeDuplicateAuthCode(clientId);
        String authCode = UUID.randomUUID()
                .toString()
                .replace("-", "");
        Instant expiry = Instant.now().plusSeconds(10);

        AUTH_CODE_STORE.put("clientId", clientId);
        AUTH_CODE_STORE.put("csrfToken", csrfToken);
        AUTH_CODE_STORE.put("customerId", customerId);
        AUTH_CODE_STORE.put("authCode", authCode);
        AUTH_CODE_STORE.put("expiry", expiry);

        return authCode;
    }

}
