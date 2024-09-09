package dev.kukukodes.KDAP.Auth.Service.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Slf4j
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Autowired
    ReactiveAuthenticationManager authenticationManager;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http.authorizeExchange(
                        Customizer -> {
                            Customizer.pathMatchers("/get").permitAll();
                            Customizer.pathMatchers("/add").permitAll();
                            Customizer.pathMatchers("/h2/**").permitAll();
                            Customizer.anyExchange().authenticated();
                        }
                )
                .authenticationManager(authenticationManager)
                .formLogin(Customizer.withDefaults())
                .csrf(Customizer -> Customizer.disable()) // only for test profile
                .build();
    }
}
