package dev.kukukodes.kdap.authenticationservice.webfilters;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Inter service communication has complete access.
 * The way it works is by encoding
 */
public class InterServiceAuthenticationFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader =exchange.getRequest().getHeaders().getFirst("Authorization");
        if(authHeader == null || !authHeader.startsWith("internal ")) {
            return chain.filter(exchange);
        }

        String encoded

        return null;
    }
}
