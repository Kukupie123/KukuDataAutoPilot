package dev.kukukodes.kdap.authenticationservice.service.oAuth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.stereotype.Service;

@Service
public class GoogleAuthService extends OAuthService {

    protected GoogleAuthService(@Autowired ReactiveClientRegistrationRepository oAuthClientRepo) {
        super("google", oAuthClientRepo);
    }
}
