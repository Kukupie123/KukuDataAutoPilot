package dev.kukukodes.KDAP.Auth.v2.components.auth.authentication.managers;

import dev.kukukodes.KDAP.Auth.v2.constants.auth.AuthConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component(AuthConstants.CustomAuthenticationManagerQualifier.OAUTH)
public class CustomOAuthAuthenticationManager implements ReactiveAuthenticationManager {
    public CustomOAuthAuthenticationManager(){
        log.info("Custom OAuth Authentication Manager");
    }
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.just(authentication);
    }
}
