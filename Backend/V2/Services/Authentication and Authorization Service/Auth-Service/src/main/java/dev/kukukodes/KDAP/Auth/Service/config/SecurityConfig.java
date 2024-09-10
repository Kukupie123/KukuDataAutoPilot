package dev.kukukodes.KDAP.Auth.Service.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

/**
 * The @Slf4j annotation enables logging functionality for this class.
 * This allows logging operations for debugging or monitoring the application's behavior.
 * The @Configuration annotation marks this class as a configuration class,
 * meaning Spring will treat it as a source of bean definitions.
 * The @EnableWebFluxSecurity annotation is crucial to enable Spring Security in WebFlux applications.
 *
 * This class configures the security aspects for the KDAP Auth Service, handling both web-based and API-based requests.
 */
@Slf4j
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /**
     * Injecting the ReactiveAuthenticationManager, which is responsible for
     * handling authentication in a reactive (non-blocking) way for our WebFlux application.
     * This allows us to customize how authentication is handled for different types of requests.
     */
    @Autowired
    ReactiveAuthenticationManager authenticationManager;

    /**
     * Defines the main security filter chain used by Spring Security in our WebFlux application.
     * It configures how requests are authenticated, what endpoints are allowed or restricted,
     * and handles both stateful (web) and stateless (API) requests.
     *
     * @param http The ServerHttpSecurity object allows configuring the security behavior.
     * @return SecurityWebFilterChain, which is a chain of security filters applied to incoming requests.
     */
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http.authorizeExchange(
                        // Customizer here defines rules for which endpoints are permitted or restricted
                        Customizer -> {
                            // Allow unrestricted access to GET and ADD endpoints
                            Customizer.pathMatchers("/get").permitAll();
                            Customizer.pathMatchers("/add").permitAll();

                            // Allow unrestricted access to H2 console (useful during development)
                            Customizer.pathMatchers("/h2/**").permitAll();

                            // Any other request must be authenticated (default rule)
                            Customizer.anyExchange().authenticated();
                        }
                )
                .httpBasic(httpBasicSpec -> {
                    // Enable HTTP Basic Authentication for API requests (stateless)
                    // Use our custom authentication manager to handle authentication
                    httpBasicSpec.authenticationManager(authenticationManager);
                })
                .formLogin(formLoginSpec -> {
                    // Disable Form Login for non-web requests (APIs don't use form login)
                    // Only enable form login for POST requests to "/login" (for web-based login forms)
                    formLoginSpec.requiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST, "/login"));

                    // Use our custom authentication manager for form-based authentication
                    formLoginSpec.authenticationManager(authenticationManager);
                })
                .csrf(csrfSpec -> {
                    // CSRF (Cross-Site Request Forgery) protection configuration
                    // CSRF is essential for stateful web-based requests but not for stateless API requests.

                    // Custom matcher to disable CSRF for API routes but enable it for web routes.
                    csrfSpec.requireCsrfProtectionMatcher(exchange -> {
                        // Check if the request path starts with "/api" (meaning it's an API request)
                        boolean isAPI = exchange.getRequest().getPath().value().startsWith("/api");

                        // If it's an API request, disable CSRF protection (stateless API doesn't need it)
                        if (isAPI) {
                            return ServerWebExchangeMatcher.MatchResult.notMatch();  // No CSRF for API
                        }

                        // Otherwise, enable CSRF protection for non-API requests (web)
                        return ServerWebExchangeMatcher.MatchResult.match();  // Enable CSRF for web
                    });
                })
                .build(); // Build the SecurityWebFilterChain with all the specified configurations
    }
}
