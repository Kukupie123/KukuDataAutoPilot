package dev.kukukodes.kdap.dataBoxService.filter;

import dev.kukukodes.kdap.dataBoxService.model.user.KDAPAuthenticatedUser;
import dev.kukukodes.kdap.dataBoxService.model.user.KDAPUser;
import dev.kukukodes.kdap.dataBoxService.helper.RequestHelper;
import dev.kukukodes.kdap.dataBoxService.service.KDAPUserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Log4j2
@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    private final RequestHelper requestHelper;
    private final KDAPUserService userService;

    public JwtTokenFilter(RequestHelper requestHelper, KDAPUserService kdapUserService) {
        this.requestHelper = requestHelper;
        this.userService = kdapUserService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("JWT Token filter active");
        String token = requestHelper.extractToken(request.getHeader("Authorization"));
        if (token != null) {
            try {
                KDAPUser userData = userService.getUserFromToken(token);
                if (userData != null) {
                    SecurityContextHolder.getContext().setAuthentication(new KDAPAuthenticatedUser(userData));
                    log.info("Authenticated user: {}", userData);
                } else {
                    log.warn("Invalid token - no user found");
                }
            } catch (Exception e) {
                log.error("Failed to process authentication token", e);
            }
        }

        filterChain.doFilter(request, response);
    }
}