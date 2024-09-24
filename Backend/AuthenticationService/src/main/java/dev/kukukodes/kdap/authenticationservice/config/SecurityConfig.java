package dev.kukukodes.kdap.authenticationservice.config;

import dev.kukukodes.kdap.authenticationservice.authenticationManagers.JwtTokenAuthenticationManager;
import dev.kukukodes.kdap.authenticationservice.webfilters.JwtTokenAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {


    @Bean
    public SecurityWebFilterChain webFilterChain(ServerHttpSecurity http, JwtTokenAuthenticationManager jwtTokenAuthenticationManager) {
        http
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/api/public/**").permitAll()
                        .pathMatchers("/api/authenticated/**").authenticated()
                        .pathMatchers("/free/**").permitAll()
                        .anyExchange().denyAll()
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .oauth2Client(Customizer.withDefaults())
                .addFilterBefore(new JwtTokenAuthenticationFilter(jwtTokenAuthenticationManager), SecurityWebFiltersOrder.AUTHENTICATION)
        ;

        return http.build();
    }
}
