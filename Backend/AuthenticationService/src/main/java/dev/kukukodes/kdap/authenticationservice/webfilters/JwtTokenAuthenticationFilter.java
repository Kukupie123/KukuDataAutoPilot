package dev.kukukodes.kdap.authenticationservice.webfilters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Filter that extracts bearer jwt token, creates authentication obj, and then authenticates it using authentication manager.
 */
@Slf4j
@Component
public class JwtTokenAuthenticationFilter implements WebFilter {

    private final ReactiveAuthenticationManager authenticationManager;

    public JwtTokenAuthenticationFilter(@Autowired ReactiveAuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        log.info("Attempting to extract token from Authorization header");
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.info("No Authorization header found");
            return chain.filter(exchange);
        }

        String token = authHeader.substring(7);
        log.info("Extracted token from header. Authenticating...");

        Authentication auth = new PreAuthenticatedAuthenticationToken(token, token);

        // Delegate authentication to AuthenticationManager
        return authenticationManager.authenticate(auth)
                .flatMap(authentication -> chain.filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication)))
                .onErrorResume(e -> {
                    log.error("JWT Authentication failed: {}", e.getMessage());
                    return chain.filter(exchange);
                });
    }
}
