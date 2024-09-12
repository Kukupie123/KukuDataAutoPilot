package dev.kukukodes.KDAP.Auth.components.auth.authentication.managers;

import dev.kukukodes.KDAP.Auth.constants.auth.AuthConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component(AuthConstants.CustomAuthenticationManagerQualifier.OAUTH)
public class CustomOAuthAuthenticationManager implements ReactiveAuthenticationManager {
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.just(authentication);
    }
}
