package dev.kukukodes.KDAP.Auth.Service.auth.components;

import dev.kukukodes.KDAP.Auth.Service.auth.constants.AuthConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Custom implementation of the {@link ReactiveAuthenticationManager} interface, designed to handle authentication
 * in a reactive manner within a Spring Security context.
 *
 * <p>This component is responsible for authenticating user credentials against the data provided by
 * the {@link CustomUserDetailsService}. It validates the username and password provided during the authentication
 * process and creates an {@link Authentication} token if the credentials are correct.
 *
 * <p>The {@link #authenticate(Authentication)} method:
 * - Extracts the username and password from the provided {@link Authentication} object.
 * - Uses the {@link CustomUserDetailsService} to retrieve user details based on the username.
 * - Compares the provided password with the stored password for validation.
 * - Creates a new {@link UsernamePasswordAuthenticationToken} with the authenticated user details if the credentials
 * are valid.
 * - Returns an error with a {@link BadCredentialsException} if the credentials are invalid or the user is not found.
 *
 * <p>This class provides a reactive approach to authentication, making use of Project Reactor's `Mono` type for asynchronous
 * processing and error handling.
 */
@Slf4j
@Component
@Qualifier(AuthConstants.CustomAuthenticationManagerQualifier.USER_PASSWORD)
public class CustomUserPasswordAuthenticationManager implements ReactiveAuthenticationManager {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    /**
     * Authenticates a user by checking the provided credentials against the stored user details.
     *
     * <p>The method retrieves user details based on the provided username, compares the provided password with
     * the stored password, and returns an {@link Authentication} token if the credentials are valid. If the
     * credentials are invalid or the user is not found, it returns a {@link Mono} error with a {@link BadCredentialsException}.
     *
     * @param authentication The authentication request containing the username and password to be validated.
     * @return A {@link Mono} containing the authenticated {@link Authentication} token if the credentials are valid,
     * or an error if they are not.
     */
    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String password = authentication.getCredentials().toString();
        String username = authentication.getName();
        log.info("Authenticating user {} with password {}", username, password);
        return customUserDetailsService.findByUsername(username)
                .flatMap(userDetails -> {
                    if (userDetails.getPassword().equals(password)) {
                        return Mono.just((Authentication) new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities()));
                    } else {
                        return Mono.error(new BadCredentialsException("Invalid username or password"));
                    }
                })
                .switchIfEmpty(Mono.error(new BadCredentialsException("Invalid username or password")));
    }
}
