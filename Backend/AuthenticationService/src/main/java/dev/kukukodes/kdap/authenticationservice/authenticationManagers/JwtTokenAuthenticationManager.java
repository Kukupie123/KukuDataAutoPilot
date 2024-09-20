package dev.kukukodes.kdap.authenticationservice.authenticationManagers;

import dev.kukukodes.kdap.authenticationservice.dto.user.UserJwtClaimsDTO;
import dev.kukukodes.kdap.authenticationservice.service.JwtService;
import dev.kukukodes.kdap.authenticationservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Expects {@link dev.kukukodes.kdap.authenticationservice.webfilters.JwtTokenAuthenticationFilter} to create Authentication object with
 * "token" set as "credential" of the authentication object.
 */
@Slf4j
@Component
public class JwtTokenAuthenticationManager implements ReactiveAuthenticationManager {
    private final JwtService jwtService;
    private final UserService userService;

    public JwtTokenAuthenticationManager(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        try {
            log.info("Authenticating JWT Token");
            PreAuthenticatedAuthenticationToken auth = (PreAuthenticatedAuthenticationToken) authentication;
            String token = (String) auth.getCredentials();
            var claims = jwtService.extractClaimsFromJwtToken(token);
            var userClaimsDTO = new UserJwtClaimsDTO(claims);
            return userService.getUserByJwtClaimsDTO(userClaimsDTO)
                    .map(user -> {
                        log.info("Successfully authenticated JWT Token request");
                        return new Authentication() {
                            @Override
                            public Collection<? extends GrantedAuthority> getAuthorities() {
                                return List.of(new SimpleGrantedAuthority(user.getId()));
                            }

                            @Override
                            public String getCredentials() {
                                return "";
                            }

                            @Override
                            public Object getDetails() {
                                return user.getEmail();
                            }

                            @Override
                            public Object getPrincipal() {
                                return user.getId();
                            }

                            @Override
                            public boolean isAuthenticated() {
                                return true;
                            }

                            @Override
                            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

                            }

                            @Override
                            public String getName() {
                                return user.getName();
                            }
                        };
                    });

        } catch (Exception e) {
            log.error("JWT Authentication Failed : {} with stack \n{}", e.getMessage(), Arrays.toString(e.getStackTrace()));
            return Mono.error(e);
        }

    }
}
