package dev.kukukodes.kdap.authenticationservice.helpers;

import dev.kukukodes.kdap.authenticationservice.models.userModels.KDAPUserAuthentication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class SecurityHelper {
    public Mono<KDAPUserAuthentication> getKDAPUserAuthentication() {
        return ReactiveSecurityContextHolder.getContext()
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("KDAP Authenticated user not found");
                    return Mono.empty();
                }))
                .map(SecurityContext::getAuthentication)
                .switchIfEmpty(Mono.error(new Exception("No authentication user found in security context")))
                .cast(KDAPUserAuthentication.class);
    }
}
