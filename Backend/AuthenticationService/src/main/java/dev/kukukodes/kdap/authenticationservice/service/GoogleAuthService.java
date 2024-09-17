package dev.kukukodes.kdap.authenticationservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class GoogleAuthService {

    public final ReactiveClientRegistrationRepository oAuthClientRepo;

    public GoogleAuthService(@Autowired ReactiveClientRegistrationRepository clientRegistrationRepository) {
        this.oAuthClientRepo = clientRegistrationRepository;
    }

    public Mono<OAuth2AuthorizationRequest> createOAuth2AuthReq() {
        return oAuthClientRepo.findByRegistrationId("google")
                .map(googleClient -> OAuth2AuthorizationRequest.authorizationCode()
                        .clientId(googleClient.getClientId())
                        .authorizationUri(googleClient.getProviderDetails().getAuthorizationUri())
                        .scope(String.join(" ", googleClient.getScopes()))
                        .redirectUri(googleClient.getRedirectUri())
                        .state(UUID.randomUUID().toString())
                        .build());

    }
}
