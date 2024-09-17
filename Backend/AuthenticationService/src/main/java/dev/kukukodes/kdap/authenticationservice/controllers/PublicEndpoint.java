package dev.kukukodes.kdap.authenticationservice.controllers;

import dev.kukukodes.kdap.authenticationservice.service.oAuth.GoogleAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/public")
public class PublicEndpoint {

    @Autowired
    GoogleAuthService googleAuthService;

    @GetMapping("/login/google")
    public Mono<ResponseEntity<String>> getGoogleLoginURL() {
        log.info("Getting URL Login");
        /*
        Client repository holds all the registered OAuth clients.
        We get google from it and use its data to create a AuthorizationRequest object.
        We then get the requestURL from this AuthReq object and return it as response
         */

        return googleAuthService.createOAuth2AuthReq()
                .map(oAuth2AuthorizationRequest -> ResponseEntity.ok(oAuth2AuthorizationRequest.getAuthorizationRequestUri()))
                .defaultIfEmpty(ResponseEntity.status(404).body("Google Provider not registered."));
    }

    /**
     * This endpoint should be hit by Google's server. Make sure it's configured as redirect URL in config.
     * <p>
     * <p>
     * Spring will automatically create AuthorizedClient upon successful redirect callback after granting permission to resources
     * <p>
     * <p>
     * Explanation:
     * - Spring Security handles the OAuth2 flow behind the scenes.
     * - When Google redirects back with the authorization code, Spring:
     * 1. Exchanges the code for access and refresh tokens
     * 2. Creates the OAuth2AuthorizedClient object
     * 3. Stores it for future use
     * <p>
     *
     * @return Generated JWT Token with user info as claims and ID as subject
     **/
    @GetMapping("/redirect/google")
    public Mono<ResponseEntity<String>> handleGoogleRedirect(ServerWebExchange exchange) {
        String code = exchange.getRequest().getQueryParams().getFirst("code");
        String state = exchange.getRequest().getQueryParams().getFirst("state");
        return googleAuthService.getTokenResponse(code, state)
                .map(accessTokenResponse -> ResponseEntity.ok(accessTokenResponse.getAccessToken().getTokenValue()));

    }


}
