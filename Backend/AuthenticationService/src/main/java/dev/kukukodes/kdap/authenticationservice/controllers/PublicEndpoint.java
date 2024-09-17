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

    final
    GoogleAuthService googleAuthService;

    public PublicEndpoint(@Autowired GoogleAuthService googleAuthService) {
        this.googleAuthService = googleAuthService;
    }

    /**
     *
     * @return URI to login to google.
     */
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
     * Redirect URL that needs to be hit by the provider.
     * This functions Authorizes the user, store the user info in database and generates JWT Token
     * @return JWT Token whose subject is userID, and user info as claims.
     */
    @GetMapping("/redirect/google")
    public Mono<ResponseEntity<String>> handleGoogleRedirect(ServerWebExchange exchange) {
        String code = exchange.getRequest().getQueryParams().getFirst("code");
        String state = exchange.getRequest().getQueryParams().getFirst("state");
        return googleAuthService.getTokenResponse(code, state)
                .map(accessTokenResponse -> ResponseEntity.ok(accessTokenResponse.getAccessToken().getTokenValue()));
    }


}
