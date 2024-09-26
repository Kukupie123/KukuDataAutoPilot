package dev.kukukodes.kdap.authenticationservice.helpers;

import dev.kukukodes.kdap.authenticationservice.models.authentication.KDAPAuthenticated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityHelper {
    private final Environment environment;

    public Mono<KDAPAuthenticated> getKDAPAuthenticated() {
        return ReactiveSecurityContextHolder.getContext()
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("KDAP Authenticated user not found");
                    return Mono.empty();
                }))
                .map(SecurityContext::getAuthentication)
                .switchIfEmpty(Mono.error(new Exception("No authentication user found in security context")))
                .cast(KDAPAuthenticated.class);
    }

    public boolean isSuperuser(String userID) {
        String[] sus = environment.getProperty("superusers").split(",");
        for (var s : sus) {
            if (s.equals(userID)) {
                return true;
            }
        }
        return false;
    }
}
