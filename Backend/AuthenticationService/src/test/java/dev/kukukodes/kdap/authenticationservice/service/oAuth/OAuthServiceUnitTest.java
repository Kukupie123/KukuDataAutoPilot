package dev.kukukodes.kdap.authenticationservice.service.oAuth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.oauth2.client.endpoint.WebClientReactiveAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponse;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

public class OAuthServiceUnitTest {

    @Mock
    private ReactiveClientRegistrationRepository oAuthClientRepo;

    @Mock
    private DefaultOAuth2UserService oidcUserService;

    @Mock
    private WebClient webClient; // Mock WebClient

    @Mock
    private WebClientReactiveAuthorizationCodeTokenResponseClient tokenResponseClient;

    @InjectMocks
    private OAuthService oAuthService = new GoogleAuthService(oAuthClientRepo);

    @BeforeEach
    void setUp() {
        openMocks(this);
        oAuthService = new OAuthService("testClientId", oAuthClientRepo, oidcUserService) {
        };
    }

    @Test
    void createOAuth2AuthReq() {
        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("test")
                .clientId("testClientId")
                .authorizationUri("http://localhost/authorize")
                .redirectUri("http://localhost/redirect")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .scope("read", "write")
                .tokenUri("http://localhost/token")
                .build();

        when(oAuthClientRepo.findByRegistrationId("testClientId")).thenReturn(Mono.just(clientRegistration));

        StepVerifier.create(oAuthService.createOAuth2AuthReq())
                .expectNextMatches(authReq ->
                        authReq.getClientId().equals("testClientId") &&
                                authReq.getAuthorizationUri().equals("http://localhost/authorize") &&
                                authReq.getRedirectUri().equals("http://localhost/redirect")
                )
                .verifyComplete();
    }

    @Test
    void createOAuth2AuthResp() {
        String authorizationCode = "authCode";
        String redirectUri = "http://localhost/redirect";
        String state = UUID.randomUUID().toString();

        OAuth2AuthorizationResponse authResponse = oAuthService.createOAuth2AuthResp(authorizationCode, redirectUri, state);

        StepVerifier.create(Mono.just(authResponse))
                .expectNextMatches(resp ->
                        resp.getCode().equals(authorizationCode) &&
                                resp.getRedirectUri().equals(redirectUri) &&
                                resp.getState().equals(state)
                )
                .verifyComplete();
    }


    @Test
    void getUserFromToken() {
        OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "testToken", null, null);

        // Mocking client registration and OAuth2User
        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("test")
                .clientId("testClientId")
                .authorizationUri("http://localhost/authorize")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .tokenUri("http://localhost/token")
                .redirectUri("http://localhost/redirect")
                .scope("read", "write")
                .build();

        OAuth2User mockUser = mock(OAuth2User.class);
        when(oAuthClientRepo.findByRegistrationId("testClientId")).thenReturn(Mono.just(clientRegistration));
        when(oidcUserService.loadUser(any(OAuth2UserRequest.class))).thenReturn(mockUser);

        StepVerifier.create(oAuthService.getUserFromToken(accessToken))
                .expectNextMatches(user -> user.equals(mockUser))
                .verifyComplete();
    }

    //TODO: Get token response test left
}
