package dev.kukukodes.kdap.authenticationservice.config;

import dev.kukukodes.kdap.authenticationservice.authenticationManagers.JwtTokenAuthenticationManager;
import dev.kukukodes.kdap.authenticationservice.service.JwtService;
import dev.kukukodes.kdap.authenticationservice.service.UserService;
import dev.kukukodes.kdap.authenticationservice.webfilters.JwtTokenAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.DelegatingReactiveAuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean()
    @Primary
    ReactiveAuthenticationManager authenticationManager(JwtService jwtService, UserService userService) {
        return new DelegatingReactiveAuthenticationManager(new JwtTokenAuthenticationManager(jwtService, userService));
    }

    @Bean
    public SecurityWebFilterChain webFilterChain(ServerHttpSecurity http, JwtService jwtService, UserService userService) {
        var authenticationManager = authenticationManager(jwtService, userService);
        http
                .authenticationManager(authenticationManager)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/api/public/**").permitAll()
                        .pathMatchers("/api/authenticated/**").authenticated()
                        .anyExchange().denyAll()
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .oauth2Client(Customizer.withDefaults())
                .addFilterBefore(new JwtTokenAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION);

        return http.build();
    }
}
