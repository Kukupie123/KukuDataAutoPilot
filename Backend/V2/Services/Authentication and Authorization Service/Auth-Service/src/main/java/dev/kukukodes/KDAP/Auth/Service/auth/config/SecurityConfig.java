package dev.kukukodes.KDAP.Auth.Service.auth.config;

import dev.kukukodes.KDAP.Auth.Service.auth.constants.AuthConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Autowired
    @Qualifier(AuthConstants.CustomAuthenticationManagerQualifier.USER_PASSWORD)
    private ReactiveAuthenticationManager userPwdAuthenticationManager;

    @Autowired
    @Qualifier(AuthConstants.CustomAuthenticationManagerQualifier.OAUTH)
    private ReactiveAuthenticationManager oAuthAuthenticationManager;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/authenticate/**", "/api/authenticate/**").permitAll()
                        .anyExchange().authenticated()
                )
                .httpBasic(httpBasic -> httpBasic
                        // Use UserPassword authentication for HTTP Basic
                        .authenticationManager(userPwdAuthenticationManager)
                )
                .oauth2Login(oauth2 -> oauth2
                        // Use OAuth authentication for OAuth2 login
                        .authenticationManager(oAuthAuthenticationManager)
                )
                .oauth2Client(oauth2Client -> oauth2Client
                        // Use OAuth authentication for OAuth2 client
                        .authenticationManager(oAuthAuthenticationManager)
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
