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

    public KDAPUser getUserFromToken(String jwtToken) throws Exception {
        log.info("Attempting to get user from token {}", jwtToken);

        var resp = authenticationServiceComs.getUserData("Bearer " + jwtToken);
        if (!resp.getStatusCode().is2xxSuccessful()) {
            throw new Exception(resp.getBody().getMessage());
        }
        return (KDAPUser) resp.getBody().getData();
    }
}
