package dev.kukukodes.kdap.dataBoxService.config;

import dev.kukukodes.kdap.dataBoxService.filter.JwtTokenFilter;
import dev.kukukodes.kdap.dataBoxService.helper.RequestHelper;
import jakarta.servlet.Filter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtTokenFilter jwtTokenFilter) throws Exception {
        http.authorizeHttpRequests(req -> req
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/api/authenticated/**").authenticated()
                        .anyRequest().denyAll()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .addFilterBefore((Filter) jwtTokenFilter, (Class<? extends Filter>) UsernamePasswordAuthenticationFilter.class)
        ;
        return http.build();
    }
}
