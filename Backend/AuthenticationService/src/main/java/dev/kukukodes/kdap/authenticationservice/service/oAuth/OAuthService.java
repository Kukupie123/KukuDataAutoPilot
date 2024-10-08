package dev.kukukodes.kdap.authenticationservice.service.oAuth;

import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.WebClientReactiveAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.security.oauth2.core.user.OAuth2User;
import reactor.core.publisher.Mono;

import java.util.UUID;

public abstract class OAuthService {
    //Required to get clientRegistration object by name
    private final String clientID;
    //Holds all the registered OAuth providers
    private final ReactiveClientRegistrationRepository oAuthClientRepo;
    //Provides function to get user using access token
    private final DefaultOAuth2UserService oIcdUserService;

    protected OAuthService(String clientID, ReactiveClientRegistrationRepository oAuthClientRepo, DefaultOAuth2UserService oidcUserService) {
        this.clientID = clientID;
        this.oAuthClientRepo = oAuthClientRepo;
        this.oIcdUserService = oidcUserService;
    }

    /**
     * Build OAuth2AuthorizationRequest object
     */
    public Mono<OAuth2AuthorizationRequest> createOAuth2AuthReq() {
        return oAuthClientRepo.findByRegistrationId(clientID)
                .map(clientRegistration -> OAuth2AuthorizationRequest.authorizationCode()
                        .clientId(clientRegistration.getClientId())
                        .authorizationUri(clientRegistration.getProviderDetails().getAuthorizationUri())
                        .scope(String.join(" ", clientRegistration.getScopes()))
                        .redirectUri(clientRegistration.getRedirectUri())
                        .state(UUID.randomUUID().toString())
                        .build());

    }

    /**
     * Build OAuth2AuthorizationResponse object. Represents the response of OAuth2 resource granted redirect.
     * All the parameters should normally be present in the redirect query parameter
     * @param authorizationCode auth code received after redirect
     * @param redirectUri the uri where client was redirected to
     * @param state state id that was used when making the request.
     * @return
     */
    public OAuth2AuthorizationResponse createOAuth2AuthResp(String authorizationCode, String redirectUri, String state) {
        return OAuth2AuthorizationResponse
                .success(authorizationCode)
                .redirectUri(redirectUri)
                .state(state)
                .build();
    }

    /**
     * Uses code and state obtained by OAuth2 providers after granting resource access to retrieve access token from resource owner.
     *
     * @return Access token
     */
    public Mono<OAuth2AccessTokenResponse> getTokenResponse(String code, String state) {
        //IMPORTANT: How to do Get Access Token Response the right way.

       /*
        The multiple abstractions and representations in this code (e.g., `OAuth2AuthorizationRequest`,
        `OAuth2AuthorizationResponse`, `OAuth2AuthorizationExchange`, etc.) follow OAuth2 best practices
        and provide a clear separation of concerns:

        1. **Modularity**: Each component represents a distinct phase in the OAuth2 flow (request, response, exchange, etc.).
        2. **Security**: These abstractions ensure proper validation, error handling, and compliance with OAuth2 protocols.
        3. **Flexibility**: The use of reactive, standardized components allows for easier maintenance, scalability, and debugging
        of the OAuth2 flow across different client providers and scenarios.

        This approach reduces the risk of errors and simplifies the process of handling token exchanges,
        without manually managing HTTP requests and responses.
        */


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
           - Retrieve the registered client’s redirect URI`.
           - Create `OAuth2AuthorizationRequest` and `OAuth2AuthorizationResponse` using the retrieved data.
           - Bundle them into an `OAuth2AuthorizationExchange`.
           - Create `OAuth2AuthorizationCodeGrantRequest` using the client details and the exchange.
           - Use `ReactiveOAuth2AccessTokenResponseClient` to send the token exchange request and retrieve the access token.

        This process follows OAuth2 protocol best practices by utilizing the provided classes for handling requests and responses.
        */

        //Get redirect uri to build OAuth2AuthorizationResponse
        Mono<String> redirectUri = this.oAuthClientRepo.findByRegistrationId(clientID).map(ClientRegistration::getRedirectUri);
        //Build OAuth2AuthorizationRequest which represents the request
        Mono<OAuth2AuthorizationRequest> authorizationRequestMono = createOAuth2AuthReq();
        //Build OAuth2AuthorizationResponse which represents the response we got.
        Mono<OAuth2AuthorizationResponse> authorizationResponseMono = redirectUri.map(redirectURI -> createOAuth2AuthResp(code, redirectURI, state));
        //Create AuthorizationExchange which represents the exchange between the authorization request and response. It doesn't actually do any exchanges, it's just a representation.
        Mono<OAuth2AuthorizationExchange> authorizationExchangeMono = Mono.zip(authorizationRequestMono, authorizationResponseMono)
                .map(objects -> new OAuth2AuthorizationExchange(objects.getT1(), objects.getT2()));
        //Create AuthorizationCodeTokenResponseClient which "exchanges" an authorization code credential for an access token credential at the Authorization Server's Token Endpoint.
        WebClientReactiveAuthorizationCodeTokenResponseClient exchanger = new WebClientReactiveAuthorizationCodeTokenResponseClient();
        //Create AuthorizationCodeGrantRequest which holds an Authorization Code credential and client registration data.
        Mono<OAuth2AuthorizationCodeGrantRequest> codeGrantRequestMono = Mono.zip(oAuthClientRepo.findByRegistrationId(clientID), authorizationExchangeMono)
                .map(objects -> new OAuth2AuthorizationCodeGrantRequest(objects.getT1(), objects.getT2()));
        //Use the authorizationCodeTokenResponse object to get a token by passing AuthorizationCodeGrantRequest object.
        return codeGrantRequestMono
                .flatMap(exchanger::getTokenResponse)
                ;
    }

    /**
     * Uses access token object to get user info from the resource server.
     *
     * @return OAuth2User
     */
    public Mono<OAuth2User> getUserFromToken(OAuth2AccessToken accessToken) {
        Mono<ClientRegistration> clientRegistrationMono = this.oAuthClientRepo.findByRegistrationId(clientID);
        Mono<OAuth2UserRequest> userRequestMono = clientRegistrationMono.map(clientRegistration -> new OAuth2UserRequest(clientRegistration, accessToken));
        return userRequestMono.map(this.oIcdUserService::loadUser);
    }
}
