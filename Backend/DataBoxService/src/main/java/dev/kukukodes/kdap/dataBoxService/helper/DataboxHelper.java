package dev.kukukodes.kdap.dataBoxService.helper;

import dev.kukukodes.kdap.dataBoxService.entity.dataBox.DataBox;
import dev.kukukodes.kdap.dataBoxService.model.ResponseModel;
import dev.kukukodes.kdap.dataBoxService.model.user.KDAPUser;
import dev.kukukodes.kdap.dataBoxService.openFeign.AuthenticationComs;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Log4j2
public class DataboxHelper {
    private final AuthenticationComs authenticationComs;
    private final ServiceCommunicationHelper serviceCommunicationHelper;

    public void validateUserID(String userId) throws Exception {
        ResponseEntity<ResponseModel<List<KDAPUser>>> authResp = authenticationComs.getUserInfoByID(userId, "Bearer " + serviceCommunicationHelper.generateToken());
        if (!authResp.getStatusCode().is2xxSuccessful()) {
            throw new Exception("Getting userinfo from authentication failed with message" + authResp.getBody().getMessage());
        }
        log.info("Got back response : {}", authResp.getBody().toString());
        if (authResp.getBody().getData() == null || authResp.getBody().getData().size() != 1) {
            throw new Exception("Expected one user info but got null or more than one or empty " + authResp.getBody());
        }
    }
    public void validateDatabox(DataBox dataBox) throws Exception {
        if (dataBox == null) throw new NullPointerException("Databox is null");
        if (dataBox.getFields() == null || dataBox.getFields().isEmpty())
            throw new Exception("Missing fields for " + dataBox);
        if (dataBox.getName() == null || dataBox.getName().isEmpty())
            throw new Exception("Missing name for databox " + dataBox);
    }
}
