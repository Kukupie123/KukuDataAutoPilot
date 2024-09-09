package dev.kukukodes.KDAP.Auth.Service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

public class KukuAuthenticationProvider implements ReactiveAuthenticationManager {
    final Logger log = LoggerFactory.getLogger(KukuAuthenticationProvider.class);

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String password = authentication.getCredentials().toString();
        String username = authentication.getName();
        log.info("Authenticating user {} with password {}", username, password);
        return Mono.just(authentication);
    }
}
