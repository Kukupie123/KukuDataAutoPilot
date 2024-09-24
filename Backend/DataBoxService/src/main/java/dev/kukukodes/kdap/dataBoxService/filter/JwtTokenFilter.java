package dev.kukukodes.kdap.dataBoxService.filter;

import dev.kukukodes.kdap.dataBoxService.model.user.KDAPUser;
import dev.kukukodes.kdap.dataBoxService.helper.RequestHelper;
import dev.kukukodes.kdap.dataBoxService.service.KDAPUserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Extracts token from bearer authorization header and creates an authentication object with its credential set to the token.
 */
@Log4j2
@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    private final RequestHelper requestHelper;

    private final KDAPUserService userService;

    public JwtTokenFilter(RequestHelper requestHelper, dev.kukukodes.kdap.dataBoxService.service.KDAPUserService kdapUserService) {
        this.requestHelper = requestHelper;
        userService = kdapUserService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = requestHelper.extractToken(request.getHeader("Authorization"));
        if (token == null) {
            log.warn("Authorization bearer Token is invalid or not found");
            filterChain.doFilter(request, response);
            return;
        }
        KDAPUser userData = userService.getUserFromToken(token);
        log.info("got user data from authentication service : {}", userData);
        if(userData == null){
            log.warn("Failed to authenticate user");
            filterChain.doFilter(request, response);
            return;
        }
        var authenticationUser = userData.getKDAPUser();
        SecurityContextHolder.setContext(new SecurityContextImpl(authenticationUser));
        filterChain.doFilter(request, response);
    }
}
