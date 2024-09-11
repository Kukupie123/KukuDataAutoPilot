package dev.kukukodes.KDAP.Auth.v2.configurations.auth;

import dev.kukukodes.KDAP.Auth.v2.components.auth.authentication.managers.CustomDelegatingAuthenticationManager;
import dev.kukukodes.KDAP.Auth.v2.components.auth.authentication.CustomJWTTokenValidationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /**
     * This refers to the {@link CustomDelegatingAuthenticationManager}
     * as the primary authentication manager in our system. <br>
     * It is responsible for delegating authentication requests to other custom authentication managers that are configured,
     * allowing multiple authentication strategies (e.g., user/password, OAuth) to be handled seamlessly. <br>
     * By using this delegating approach, we ensure that different authentication mechanisms are supported within a unified flow.
     */

    @Autowired
    ReactiveAuthenticationManager authenticationManager;
    @Autowired
    CustomJWTTokenValidationFilter jwtTokenValidationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .authenticationManager(authenticationManager)
                .addFilterBefore(jwtTokenValidationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/authenticate/**", "/api/authenticate/**").permitAll()
                        .anyExchange().authenticated()
                )
                .formLogin(formLogin -> formLogin.disable())
                .csrf(csrf -> csrf.requireCsrfProtectionMatcher(exchange -> {
                    boolean isAPI = exchange.getRequest().getPath().value().startsWith("/api/");
                    return isAPI ? ServerWebExchangeMatcher.MatchResult.notMatch()
                            : ServerWebExchangeMatcher.MatchResult.match();
                }))
                .build();
    }
}
