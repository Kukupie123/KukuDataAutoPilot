package dev.kukukodes.kdap.authenticationservice.controllers;

import dev.kukukodes.kdap.authenticationservice.service.GoogleAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.client.endpoint.*;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

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

        /*
    Manually handling the token exchange process the hard & right way.

    The process is done by using OAuth2-provided classes instead of directly sending requests via WebClient.
    This allows for better flexibility and follows OAuth2 best practices.

    Steps:

    1. Create `OAuth2AuthorizationRequest`:
       Represents the OAuth2 Authorization Request. This object contains details like client ID, scopes, and redirect URI.
       It's used to initiate the OAuth2 Authorization request from the client to the authorization server.

    2. Create `OAuth2AuthorizationResponse`:
       Captures the response data from the authorization server after the user approves or denies the consent.
       It contains the authorization code or error information along with state and redirect URI.

    3. Create `OAuth2AuthorizationExchange`:
       Represents the exchange between the authorization request and response.
       It links the initial request with the response to facilitate the token exchange process.

    4. Create `ReactiveOAuth2AccessTokenResponseClient`:
       A client interface responsible for making the request to the authorization server to exchange the authorization code for an access token.
       The concrete implementation `WebClientReactiveAuthorizationCodeTokenResponseClient` is used here to handle the request reactively.

    5. Create `OAuth2AuthorizationCodeGrantRequest`:
       Represents the actual request for an OAuth 2.0 authorization code grant.
       This object packages the client information (`ClientRegistration`) and the authorization exchange (`OAuth2AuthorizationExchange`).

    6. Token Exchange Process:
       - Extract `code` and `state` from the authorization response.
       - Retrieve the registered client’s redirect URI using `googleAuthService`.
       - Create `OAuth2AuthorizationRequest` and `OAuth2AuthorizationResponse` using the retrieved data.
       - Bundle them into an `OAuth2AuthorizationExchange`.
       - Create `OAuth2AuthorizationCodeGrantRequest` using the client details and the exchange.
       - Use `ReactiveOAuth2AccessTokenResponseClient` to send the token exchange request and retrieve the access token.

    This process follows OAuth2 protocol best practices by utilizing the provided classes for handling requests and responses.
*/


        String code = exchange.getRequest().getQueryParams().getFirst("code");
        String state = exchange.getRequest().getQueryParams().getFirst("state");

        //Get redirect uri to build OAuth2AuthorizationResponse
        Mono<String> redirectUri = googleAuthService.oAuthClientRepo.findByRegistrationId("google").map(ClientRegistration::getRedirectUri);
        //Build OAuth2AuthorizationRequest which represents the request
        Mono<OAuth2AuthorizationRequest> authorizationRequestMono = googleAuthService.createOAuth2AuthReq();
        //Build OAuth2AuthorizationResponse which represents the response we got.
        Mono<OAuth2AuthorizationResponse> authorizationResponseMono = redirectUri.map(redirectURI -> OAuth2AuthorizationResponse
                .success(code)
                .redirectUri(redirectURI)
                .state(state)
                .build());
        //Create AuthorizationExchange which represents the exchange between the authorization request and response. It doesn't actually do any exchanges, it's just a representation.
        Mono<OAuth2AuthorizationExchange> authorizationExchangeMono = Mono.zip(authorizationRequestMono, authorizationResponseMono)
                .map(objects -> new OAuth2AuthorizationExchange(objects.getT1(), objects.getT2()));
        //Create AuthorizationCodeTokenResponseClient which "exchanges" an authorization code credential for an access token credential at the Authorization Server's Token Endpoint.
        ReactiveOAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> exchanger = new WebClientReactiveAuthorizationCodeTokenResponseClient();
        //Create AuthorizationCodeGrantRequest which holds an Authorization Code credential and client registration data.
        Mono<OAuth2AuthorizationCodeGrantRequest> codeGrantRequestMono = Mono.zip(googleAuthService.oAuthClientRepo.findByRegistrationId("google"), authorizationExchangeMono)
                .map(objects -> new OAuth2AuthorizationCodeGrantRequest(objects.getT1(), objects.getT2()));
        //Use the authorizationCodeTokenResponse object to get a token by passing AuthorizationCodeGrantRequest object.
        Mono<OAuth2AccessTokenResponse> tokenResponseMono = codeGrantRequestMono.flatMap(exchanger::getTokenResponse);
        return tokenResponseMono.map(tokenResponse -> ResponseEntity.ok(tokenResponse.getAccessToken().getTokenValue()));
    }


}
