package dev.kukukodes.kdap.dataBoxService.helper;

import dev.kukukodes.kdap.dataBoxService.model.ResponseModel;
import dev.kukukodes.kdap.dataBoxService.model.user.KDAPUser;
import dev.kukukodes.kdap.dataBoxService.openFeign.AuthenticationComs;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Log4j2
public class RequestHelper {
    private final AuthenticationComs authenticationComs;
    private final ServiceCommunicationHelper serviceCommunicationHelper;
    @Getter
    protected final AuthenticationRequest authenticationRequest = new AuthenticationRequest();

    public String extractToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        return authorization.substring("Bearer ".length());
    }

    public class AuthenticationRequest {
        public KDAPUser getUserInfo(String userId) throws Exception {
            ResponseEntity<ResponseModel<List<KDAPUser>>> authResp = authenticationComs.getUserInfoByID(userId, "Bearer " + serviceCommunicationHelper.generateToken());
            if (!authResp.getStatusCode().is2xxSuccessful()) {
                throw new Exception("Getting userinfo from authentication failed with message" + authResp.getBody().getMessage());
            }
            log.info("Got back response : {}", authResp.getBody().toString());
            if (authResp.getBody().getData() == null || authResp.getBody().getData().size() != 1) {
                throw new Exception("Expected one user info but got null or more than one or empty " + authResp.getBody());
            }
            return authResp.getBody().getData().get(0);
        }
    }
}
