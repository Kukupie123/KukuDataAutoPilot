package dev.kukukodes.KDAP.Auth.Service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class CustomAuthenticationManager implements ReactiveAuthenticationManager {
    final Logger log = LoggerFactory.getLogger(CustomAuthenticationManager.class);

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String password = authentication.getCredentials().toString();
        String username = authentication.getName();
        log.info("Authenticating user {} with password {}", username, password);
        return Mono.just(authentication);
    }
}
