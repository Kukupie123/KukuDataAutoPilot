package dev.kukukodes.kdap.authenticationservice.authenticationConverter;

import dev.kukukodes.kdap.authenticationservice.constants.RequestSourceConst;
import dev.kukukodes.kdap.authenticationservice.helpers.RequestHelper;
import dev.kukukodes.kdap.authenticationservice.models.authentication.KDAPPreAuthentication;
import dev.kukukodes.kdap.authenticationservice.service.JwtService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenToKDAPPreAuthenticatedConverter implements ServerAuthenticationConverter {
    private final RequestHelper requestHelper;
    private final JwtService jwtService;

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        String token = requestHelper.extractToken(authHeader);
        if (token == null) {
            log.warn("No Bearer token found in header");
            return Mono.empty();
        }

        try {
            Claims claims = jwtService.extractClaimsFromJwtToken(token);
            String source = claims.get("source", String.class);
            if (!(source.equals(RequestSourceConst.CLIENT) || source.equals(RequestSourceConst.INTERNAL))) {
                return Mono.error(new RuntimeException(String.format("Invalid 'source' (%s) in jwt token. It must be %s or %s", source, RequestSourceConst.CLIENT, RequestSourceConst.INTERNAL)));
            }
            KDAPPreAuthentication kdapPreAuthentication = new KDAPPreAuthentication(source, claims.getSubject());
            log.info("Created KDAPPreAuthentication object from token in header {}", kdapPreAuthentication);
            return Mono.just(kdapPreAuthentication);

        } catch (Exception e) {
            log.error("Exception at filter {}", e.getMessage());
            return Mono.empty();
        }
    }
}
