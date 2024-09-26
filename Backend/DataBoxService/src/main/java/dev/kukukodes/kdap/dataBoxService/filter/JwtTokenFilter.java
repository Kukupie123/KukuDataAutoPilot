package dev.kukukodes.kdap.dataBoxService.filter;

import dev.kukukodes.kdap.dataBoxService.helper.RequestHelper;
import dev.kukukodes.kdap.dataBoxService.model.ResponseModel;
import dev.kukukodes.kdap.dataBoxService.model.user.KDAPAuthenticated;
import dev.kukukodes.kdap.dataBoxService.model.user.KDAPUser;
import dev.kukukodes.kdap.dataBoxService.openFeign.AuthenticationComs;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.naming.CommunicationException;
import java.io.IOException;
import java.util.List;

@Log4j2
@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    private final RequestHelper requestHelper;
    private final AuthenticationComs authenticationComs;

    public JwtTokenFilter(RequestHelper requestHelper, AuthenticationComs authenticationComs) {
        this.requestHelper = requestHelper;
        this.authenticationComs = authenticationComs;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("JWT Token filter active");
        String token = requestHelper.extractToken(request.getHeader("Authorization"));
        if (token != null) {
            try {
                ResponseEntity<ResponseModel<List<KDAPUser>>> authResp = authenticationComs.getUserInfoFromClientToken(token);
                if (!authResp.getStatusCode().is2xxSuccessful()) {
                    throw new CommunicationException(authResp.getBody().getMessage());
                }
                if (authResp.getBody().getData() == null || authResp.getBody().getData().size() != 1) {
                    throw new CommunicationException("Received empty or more than 1 users, was expecting 1" + authResp.getBody().getMessage());
                }
                KDAPUser userData = authResp.getBody().getData().get(0);
                if (userData != null) {
                    SecurityContextHolder.getContext().setAuthentication(new KDAPAuthenticated(userData));
                    log.info("Authenticated user: {}", userData);
                } else {
                    log.warn("Invalid token - no user found");
                }
            } catch (Exception e) {
                log.error("Failed to process authentication token", e);
                filterChain.doFilter(request, response);
            }
        }
        log.info("No auth token found");
        filterChain.doFilter(request, response);
    }
}