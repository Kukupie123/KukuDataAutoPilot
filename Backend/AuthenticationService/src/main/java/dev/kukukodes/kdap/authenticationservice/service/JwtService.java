package dev.kukukodes.kdap.authenticationservice.service;

import dev.kukukodes.kdap.authenticationservice.entity.UserEntity;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    public String generateUserJwtToken(UserEntity user) {
        return "THIS IS DUMMY JWT for " + user.toString();
    }
}
