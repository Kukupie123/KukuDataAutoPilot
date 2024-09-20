package dev.kukukodes.kdap.authenticationservice.helpers;

import org.springframework.stereotype.Service;

@Service
public class RequestHelper

{

    public String extractToken(String authorization){
        if(authorization == null || !authorization.startsWith("Bearer ")){
            return null;
        }
        return authorization.substring("Bearer ".length());
    }
}
