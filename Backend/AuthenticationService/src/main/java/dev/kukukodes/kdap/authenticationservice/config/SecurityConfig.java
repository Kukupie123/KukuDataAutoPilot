package dev.kukukodes.kdap.authenticationservice.config;

import dev.kukukodes.kdap.authenticationservice.authenticationConverter.JwtTokenToKDAPPreAuthenticatedConverter;
import dev.kukukodes.kdap.authenticationservice.authenticationManagers.KDAPAuthenticationManager;
import dev.kukukodes.kdap.authenticationservice.constants.AccessLevelConst;
import dev.kukukodes.kdap.authenticationservice.helpers.RequestHelper;
import dev.kukukodes.kdap.authenticationservice.helpers.SecurityHelper;
import dev.kukukodes.kdap.authenticationservice.service.JwtService;
import dev.kukukodes.kdap.authenticationservice.service.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain webFilterChain(ServerHttpSecurity http, RequestHelper requestHelper, JwtService jwtService, @Qualifier(AccessLevelConst.ADMIN) UserService userService, SecurityHelper securityHelper) {
        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(new KDAPAuthenticationManager(userService, securityHelper));
        authenticationWebFilter.setServerAuthenticationConverter(new JwtTokenToKDAPPreAuthenticatedConverter(requestHelper, jwtService));
        return http
                .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .oauth2Login(Customizer.withDefaults())
                .oauth2Client(Customizer.withDefaults())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/api/public/**").permitAll()
                        .pathMatchers("/api/authenticated/**").authenticated()
                        .anyExchange().denyAll()
                )
                .build();
    }
}