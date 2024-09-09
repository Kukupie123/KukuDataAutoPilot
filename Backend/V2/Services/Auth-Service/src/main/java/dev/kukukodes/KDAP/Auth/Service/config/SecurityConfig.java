package dev.kukukodes.KDAP.Auth.Service.config;


import dev.kukukodes.KDAP.Auth.Service.service.KukuAuthenticationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;


@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    Logger log = LoggerFactory.getLogger(SecurityConfig.class);


    @Bean
    public ReactiveAuthenticationManager authenticationManager() {
        return new KukuAuthenticationProvider();
    }


    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http.authorizeExchange(Customizer -> Customizer.anyExchange().authenticated())
                .authenticationManager(authenticationManager())
                .formLogin(Customizer.withDefaults())
                .build();
    }
}
