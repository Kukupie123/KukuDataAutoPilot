package dev.kukukodes.kdap.authenticationservice.authenticationManagers;

import dev.kukukodes.kdap.authenticationservice.enums.UserRole;
import dev.kukukodes.kdap.authenticationservice.models.KDAPUserAuthentication;
import dev.kukukodes.kdap.authenticationservice.repo.UserRepo;
import dev.kukukodes.kdap.authenticationservice.service.CacheService;
import dev.kukukodes.kdap.authenticationservice.service.JwtService;
import dev.kukukodes.kdap.authenticationservice.service.UserService;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class JwtTokenAuthenticationManager implements ReactiveAuthenticationManager {
    private final JwtService jwtService;
    private final UserService userService;
    private final String superEmail;
    //Direct access to userRepo required because userService's operations valid authenticated user
    private final UserRepo userRepo;
    private final CacheService cacherService;

    public JwtTokenAuthenticationManager(JwtService jwtService, UserService userService, @Value("${superemail}") String superEmail, UserRepo userRepo, CacheService cacherService) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.superEmail = superEmail;
        this.userRepo = userRepo;
        this.cacherService = cacherService;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.justOrEmpty(authentication)
                .filter(auth -> auth instanceof PreAuthenticatedAuthenticationToken)
                .cast(PreAuthenticatedAuthenticationToken.class)
                .map(PreAuthenticatedAuthenticationToken::getCredentials)
                .cast(String.class)
                .flatMap(this::authenticateToken)
                .doOnError(e -> log.error("JWT Authentication Failed: {}", e.getMessage()))
                .onErrorResume(e -> Mono.empty());
    }

    private Mono<Authentication> authenticateToken(String token) {
        return Mono.fromCallable(() -> jwtService.extractClaimsFromJwtToken(token))
                .map(Claims::getSubject)
                .flatMap(userID -> {
                    var user = cacherService.getUser(userID);
                    if (user == null) {
                        return userRepo.getUserByID(userID).doOnSuccess(cacherService::cacheUser);
                    }
                    return Mono.justOrEmpty(user);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("User not found");
                    return Mono.empty();
                }))
                .map(user -> {
                    log.info("Successfully authenticated JWT Token request");
                    UserRole role = user.getEmail().equals(superEmail) ? UserRole.ADMIN : UserRole.USER;
                    log.info(role == UserRole.ADMIN ? "Logged in as super user" : "Logged in as regular user");
                    return new KDAPUserAuthentication(token, user.getId(), role, true);
                })
                ;
    }
}
