package dev.kukukodes.kdap.authenticationservice.authenticationManagers;

import dev.kukukodes.kdap.authenticationservice.constants.AccessLevelConst;
import dev.kukukodes.kdap.authenticationservice.entity.user.KDAPUserEntity;
import dev.kukukodes.kdap.authenticationservice.models.authentication.KDAPAuthenticated;
import dev.kukukodes.kdap.authenticationservice.models.authentication.KDAPPreAuthentication;
import dev.kukukodes.kdap.authenticationservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

import java.util.Arrays;

import static dev.kukukodes.kdap.authenticationservice.constants.RequestSourceConst.CLIENT;
import static dev.kukukodes.kdap.authenticationservice.constants.RequestSourceConst.INTERNAL;

/**
 * Uses {@link  dev.kukukodes.kdap.authenticationservice.models.authentication.KDAPPreAuthentication} to create {@link KDAPAuthenticated}
 */
@Slf4j
public class KDAPAuthenticationManager implements ReactiveAuthenticationManager {
    private final Environment environment;
    private final UserService userService;

    public KDAPAuthenticationManager(Environment environment, @Qualifier(AccessLevelConst.ADMIN) UserService userService) {
        this.environment = environment;
        this.userService = userService;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        log.info("KDAP Authentication Service Authentication Manager");
        if (!(authentication instanceof KDAPPreAuthentication preAuthentication)) {
            log.warn("Skipping authentication as authentication is not of type KDAPPreAuthentication");
            return Mono.just(authentication);
        }
        //Determine the access level.
        String accessLevel;
        switch (preAuthentication.getSource()) {
            case CLIENT -> {
                //Check if ID is in superusers list of configuration
                if (Arrays.stream(environment.getProperty("superusers").split(",")).anyMatch(s -> s.equals(preAuthentication.getId()))) {
                    accessLevel = AccessLevelConst.ADMIN;
                    break;
                }
                accessLevel = AccessLevelConst.SELF;
            }
            case INTERNAL -> accessLevel = AccessLevelConst.ADMIN;
            default -> throw new IllegalStateException("Unexpected source value: " + preAuthentication.getSource());
        }
        log.info("Access level = {}", accessLevel);
        //Get user and store it in authentication if it's client request
        KDAPUserEntity user;
        Mono<KDAPAuthenticated> kdapAuthenticatedMono;
        if (preAuthentication.getSource().equals(CLIENT)) {
            log.info("Requester is a client getting user from userService");
            kdapAuthenticatedMono = userService.getUserById(preAuthentication.getId()).map(
                    dbUser -> new KDAPAuthenticated(accessLevel, preAuthentication.getSource(), dbUser)
            )
            ;
        } else {
            log.info("Requester is not a client. Setting user to null");
            kdapAuthenticatedMono = Mono.just(new KDAPAuthenticated(accessLevel, preAuthentication.getSource(), null));
        }
        return kdapAuthenticatedMono.cast(Authentication.class);
    }


}
