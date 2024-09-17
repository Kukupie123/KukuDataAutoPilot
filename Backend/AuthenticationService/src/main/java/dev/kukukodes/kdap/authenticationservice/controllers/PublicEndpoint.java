package dev.kukukodes.kdap.authenticationservice.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/public")
public class PublicEndpoint {
    @GetMapping("/login/google")
    public Mono<ResponseEntity<String>> getGoogleLoginURL(@Autowired ReactiveClientRegistrationRepository registeredOAuthClients) {
        log.info("Getting URL Login");
        /*
        Client repository holds all the registered OAuth clients.
        We get google from it and use its data to create a AuthorizationRequest object.
        We then get the requestURL from this AuthReq object and return it as response
         */
        Mono<ClientRegistration> googleClientRegistration = registeredOAuthClients.findByRegistrationId("google");
        return googleClientRegistration.map(googleClient -> {
            String scopes = String.join(" ", googleClient.getScopes());
            OAuth2AuthorizationRequest OAuthReq = OAuth2AuthorizationRequest.authorizationCode().clientId(System.getenv(googleClient.getClientId())).authorizationUri(googleClient.getProviderDetails().getAuthorizationUri()).scope(scopes).redirectUri(googleClient.getRedirectUri()).state(UUID.randomUUID().toString()).build();
            String url = OAuthReq.getAuthorizationRequestUri();
            log.info("Generated URL : {}", url);
            return ResponseEntity.ok().body(url);
        }).defaultIfEmpty(ResponseEntity.status(404).body("Google Provider not registered."));
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
     *
     * @param authorizedClient (Auto injected) Once the end-user (aka the Resource Owner) grants permissions to the client to access its resources, an OAuth2AuthorizedClient entity is created.
     *                         It'll be responsible for associating access tokens to client registrations and resource owners (represented by Principal objects).
     *                         <p>
     * @param oidcUser         (Auto Injected) Represents authenticated OID user. This is set automatically upon successful granting of permission.
     *                         <p>
     * @return Generated JWT Token with user info as claims and ID as subject
     **/
    @GetMapping("/redirect/google")
    public Mono<ResponseEntity<String>> handleGoogleRedirect(
            @RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient,
            @AuthenticationPrincipal OidcUser oidcUser) {

        return Mono.fromCallable(() -> {
            log.info("Handling Google OAuth2 redirect for user: {}", oidcUser.getEmail());
            OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
            OidcUserInfo userInfo = oidcUser.getUserInfo();

            // Here you would typically generate a JWT token
            // String jwtToken = jwtService.generateToken(userInfo);

            Map<String, Object> response = new HashMap<>();
            response.put("email", userInfo.getEmail());
            response.put("name", userInfo.getFullName());

            return ResponseEntity.ok(response.toString());
        }).onErrorResume(e -> {
            log.error("Error during OAuth2 redirect handling", e);
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Authentication failed"));
        });
    }
}
