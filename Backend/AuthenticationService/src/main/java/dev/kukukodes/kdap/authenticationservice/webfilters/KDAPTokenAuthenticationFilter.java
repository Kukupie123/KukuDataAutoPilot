package dev.kukukodes.kdap.authenticationservice.webfilters;

import dev.kukukodes.kdap.authenticationservice.constants.RequestSourceConst;
import dev.kukukodes.kdap.authenticationservice.helpers.RequestHelper;
import dev.kukukodes.kdap.authenticationservice.models.authentication.KDAPPreAuthentication;
import dev.kukukodes.kdap.authenticationservice.service.JwtService;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;


/**
 * Validates bearer token. Creates KDAPPreAuthentication object
 * It is then passed on to KDAP Authentication Manager
 */
@Slf4j
public class KDAPTokenAuthenticationFilter implements WebFilter {

    private final RequestHelper requestHelper;
    private final JwtService jwtService;

    public KDAPTokenAuthenticationFilter(RequestHelper requestHelper, JwtService jwtService) {
        this.requestHelper = requestHelper;
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        //Determine the source
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        String token = requestHelper.extractToken(authHeader);
        if (token == null) {
            log.warn("No Bearer token found in header");
            return chain.filter(exchange);
        }
        Claims claims;
        try {
            claims = jwtService.extractClaimsFromJwtToken(token);
        } catch (Exception e) {
            log.error("Exception at filter {}", e.getMessage());
            return chain.filter(exchange);
        }
        String source = claims.get("source", String.class);
        if (!(source.equals(RequestSourceConst.CLIENT) || source.equals(RequestSourceConst.INTERNAL))) {
            throw new RuntimeException(String.format("Invalid 'source' (%s) in jwt token. It must be %s or %s", source, RequestSourceConst.CLIENT, RequestSourceConst.INTERNAL));
        }
        //Create KDAPPreAuthentication object and set it as authentication
        KDAPPreAuthentication kdapPreAuthentication = new KDAPPreAuthentication(source, claims.getSubject());
        return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(kdapPreAuthentication));
    }
}
