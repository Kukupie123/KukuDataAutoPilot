package dev.kukukodes.kdap.dataBoxService.helper;

import org.springframework.stereotype.Component;

@Component
public class RequestHelper {
    public String extractToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        return authorization.substring("Bearer ".length());
    }
}
