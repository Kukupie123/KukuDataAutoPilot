package dev.kukukodes.KDAP.Auth.Service.auth.components;

import dev.kukukodes.KDAP.Auth.Service.auth.constants.AuthConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@Qualifier(AuthConstants.CustomAuthenticationManagerQualifier.OAUTH)
public class CustomOAuthAuthenticationManager implements ReactiveAuthenticationManager {
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.just(authentication);
    }
}
