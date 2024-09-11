package dev.kukukodes.KDAP.Auth.Service.auth.components.AuthenticationManagers;

import dev.kukukodes.KDAP.Auth.Service.auth.components.UserDetailsService.CustomUserDetailsService;
import dev.kukukodes.KDAP.Auth.Service.auth.constants.AuthConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component(AuthConstants.CustomAuthenticationManagerQualifier.USER_PASSWORD)
public class CustomUserPasswordAuthenticationManager implements ReactiveAuthenticationManager {
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String password = authentication.getCredentials().toString();
        String username = authentication.getName();
        log.info("Authenticating user {} with password {}", username, password);
        return customUserDetailsService.findByUsername(username)
                .switchIfEmpty(Mono.error(new BadCredentialsException("User not found")))
                .flatMap(userDetails -> {
                    if (userDetails.getPassword().equals(password)) {
                        return Mono.just((Authentication) new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities()));
                    } else {
                        return Mono.error(new BadCredentialsException("Invalid password"));
                    }
                });
    }
}
