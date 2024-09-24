package dev.kukukodes.kdap.dataBoxService.service;

import dev.kukukodes.kdap.dataBoxService.model.user.KDAPUser;
import dev.kukukodes.kdap.dataBoxService.interServiceCommunication.clients.AuthenticationServiceComs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KDAPUserService {
    private final AuthenticationServiceComs authenticationServiceComs;

    public KDAPUserService(AuthenticationServiceComs authenticationServiceComs) {
        this.authenticationServiceComs = authenticationServiceComs;
    }

    public KDAPUser getUserFromToken(String jwtToken){
        log.info("Attempting to get user from token {}", jwtToken);
        return authenticationServiceComs.getUserData("Bearer " + jwtToken);
    }
}
