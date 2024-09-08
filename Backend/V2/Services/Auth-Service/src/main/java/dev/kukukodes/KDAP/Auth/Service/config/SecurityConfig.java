package dev.kukukodes.KDAP.Auth.Service.config;

import io.netty.util.internal.StringUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Base64;

@Configuration //Mark this class as a configuration
@EnableWebSecurity
public class SecurityConfig {
    public Mono<Authentication> authenticationConverter(ServerWebExchange exchange) {
        ServerHttpRequest req = exchange.getRequest();
        //get body and extract it's id and password from it
    }
}
