package com.Banking.SelfBuild.Self.Build.Service;

import com.Banking.SelfBuild.Self.Build.Entity.CustomerSession;
import com.Banking.SelfBuild.Self.Build.POJO.SessionManager;
import com.Banking.SelfBuild.Self.Build.Repository.CustomerSessionRepo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class LogOffProviderService
{
    @Autowired
    SessionManager sessionManager;

    private static final Logger log = LogManager.getLogger(LogOffProviderService.class);


    @Autowired
    private CustomerSessionRepo customerSessionRepo;

    public Map<String, Object> logout(String dspSession) {

        List<Map<String, Object>> sessions = sessionManager.getList();

        if (sessions.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Error resolving user from JSON");
        }

        Iterator<Map<String, Object>> iterator = sessions.iterator();

        while (iterator.hasNext()) {
            Map<String, Object> session = iterator.next();

            if (!dspSession.equals(session.get("dspSession"))) {
                continue;
            }

            Instant expiry = (Instant) session.get("expiry");

            if (Instant.now().isAfter(expiry)) {
                iterator.remove();
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Error resolving user from JSON");
            }

            // Save logout audit
            CustomerSession customerSession = new CustomerSession();
            customerSession.setDspSession(dspSession);
            customerSession.setIssuedAt(session.get("issuedAt").toString());
            customerSession.setExpired(expiry.toString());
            customerSession.setSession_correlationId(
                    session.get("session_correlationId").toString());
            customerSession.setCustomerId(
                    session.get("customerId").toString());

            customerSessionRepo.save(customerSession);

            iterator.remove();
            log.info("Successfully logged out session {}", dspSession);

            return Map.of("message", "Successfully logged out of all sessions");
        }

        throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "Error resolving user from JSON");
    }
}
