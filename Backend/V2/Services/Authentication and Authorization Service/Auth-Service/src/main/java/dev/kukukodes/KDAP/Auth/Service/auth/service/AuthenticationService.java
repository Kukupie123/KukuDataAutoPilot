package dev.kukukodes.KDAP.Auth.Service.auth.service;

import dev.kukukodes.KDAP.Auth.Service.auth.components.AuthenticationManagers.CustomUserPasswordAuthenticationManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class AuthenticationService {
    @Autowired
    CustomUserPasswordAuthenticationManager userPwdAuthManager;

    public Mono<Boolean> authenticateUserAndPassword(String username, String password) {
        return userPwdAuthManager.
                authenticate(new UsernamePasswordAuthenticationToken(username, password))
                .map(authentication -> authentication.isAuthenticated());
    }
}
