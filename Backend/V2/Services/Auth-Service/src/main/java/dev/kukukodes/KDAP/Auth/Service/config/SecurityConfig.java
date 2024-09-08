package dev.kukukodes.KDAP.Auth.Service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration //Mark this class as a configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(authRequest -> {
            authRequest.requestMatchers("/login").permitAll();
            authRequest.anyRequest().authenticated();
        }).formLogin(Customizer.withDefaults()).build();
    }
}
