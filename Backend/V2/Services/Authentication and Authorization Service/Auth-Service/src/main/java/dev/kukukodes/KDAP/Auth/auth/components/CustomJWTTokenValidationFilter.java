package dev.kukukodes.KDAP.Auth.auth.components;

import dev.kukukodes.KDAP.Auth.jwt.service.JWTService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class CustomJWTTokenValidationFilter implements WebFilter {

    @Autowired
    private JWTService jwtService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        log.info("CustomJWTTokenValidationFilter");
        // Extract the authentication from the security context. Security context can be empty if there is no authentication done yet
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> {
                    log.info("Extracting Authentication from Security context");
                    return securityContext.getAuthentication();
                })
                .flatMap(authentication -> {
                    if (authentication.isAuthenticated()) {
                        log.info("Authentication is authenticated");
                    } else {
                        log.info("Authentication is not authenticated. Validating Bearer Token");
                        try {
                            var claims = validateToken(exchange);
                        } catch (Exception e) {
                            return Mono.error(e);
                        }
                    }
                    return chain.filter(exchange);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("No Authentication found. Validating Bearer Token");
                    try {
                        var claims = validateToken(exchange);
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                    return chain.filter(exchange);
                }));

    }

    Claims validateToken(ServerWebExchange exchange) throws JwtException {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.info("No Bearer token provided, Skipping JWT Token validation");
            return null;
        }
        log.info("Validating Bearer Token");
        String token = authHeader.substring(7);
        Claims claims = jwtService.extractClaims(token);
        log.info("Extracted claims are: {}", claims);
        return claims;
    }
}
