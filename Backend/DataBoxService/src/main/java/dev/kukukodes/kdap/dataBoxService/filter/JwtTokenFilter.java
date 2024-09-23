package dev.kukukodes.kdap.dataBoxService.filter;

import com.fasterxml.jackson.databind.json.JsonMapper;
import dev.kukukodes.kdap.dataBoxService.dto.UserDataDTO;
import dev.kukukodes.kdap.dataBoxService.helper.RequestHelper;
import dev.kukukodes.kdap.dataBoxService.model.KDAPUser;
import dev.kukukodes.kdap.dataBoxService.openFeign.clients.OFAuthenticationClient;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

/**
 * Extracts token from bearer authorization header and creates an authentication object with its credential set to the token.
 */
@Log4j2
@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    private final RequestHelper requestHelper;

    private final OFAuthenticationClient authenticationClient;

    private final JsonMapper jsonMapper;

    public JwtTokenFilter(RequestHelper requestHelper, OFAuthenticationClient authenticationClient, JsonMapper jsonMapper) {
        this.requestHelper = requestHelper;
        this.authenticationClient = authenticationClient;
        this.jsonMapper = jsonMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = requestHelper.extractToken(request.getHeader("Authorization"));
        if (token == null) {
            log.warn("Authorization bearer Token is invalid or not found");
            filterChain.doFilter(request, response);
            return;
        }

        ResponseEntity<String> userData = authenticationClient.getUserData("Bearer "+token);
//        var restClient = RestClient.create();
//        var authResp = restClient.get()
//                .uri(URI.create("http://localhost:8080/api/authenticated/"))
//                .header("Authorization", "Bearer " + token)
//                .header("Accept", "application/json")
//                .header("Content-Type", "application/json")
//                .retrieve();
//
//        var userDataResp = authResp.toEntity(UserData.class);
//        if (!userDataResp.getStatusCode().is2xxSuccessful()) {
//            log.error("Failed to get user data from token");
//            filterChain.doFilter(request, response);
//            return;
//        }
//        var userData = userDataResp.getBody();

        log.info("got user data from authentication service : {}", userData);
        var user = jsonMapper.readValue(userData.getBody(), UserDataDTO.class);
        var authenticationUser = Objects.requireNonNull(user).getKDAPUser();
        SecurityContextHolder.setContext(new SecurityContextImpl(authenticationUser));
        filterChain.doFilter(request, response);
    }
}
