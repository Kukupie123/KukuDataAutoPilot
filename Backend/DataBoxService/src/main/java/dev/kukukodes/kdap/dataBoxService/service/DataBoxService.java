package dev.kukukodes.kdap.dataBoxService.service;

import dev.kukukodes.kdap.dataBoxService.constants.AccessLevelConst;
import dev.kukukodes.kdap.dataBoxService.entity.dataBox.DataBox;
import dev.kukukodes.kdap.dataBoxService.helper.SecurityHelper;
import dev.kukukodes.kdap.dataBoxService.helper.ServiceCommunicationHelper;
import dev.kukukodes.kdap.dataBoxService.model.ResponseModel;
import dev.kukukodes.kdap.dataBoxService.model.user.KDAPAuthenticated;
import dev.kukukodes.kdap.dataBoxService.model.user.KDAPUser;
import dev.kukukodes.kdap.dataBoxService.openFeign.AuthenticationComs;
import dev.kukukodes.kdap.dataBoxService.publisher.DataBoxPublisher;
import dev.kukukodes.kdap.dataBoxService.repo.IDataBoxRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataBoxService {
    private final IDataBoxRepo dataBoxRepo;
    private final SecurityHelper securityHelper;
    private final CacheService cacheService;
    private final DataBoxPublisher dataBoxPublisher;
    private final AuthenticationComs authenticationComs;
    private final ServiceCommunicationHelper serviceCommunicationHelper;

    public DataBox addDatabox(DataBox dataBox) throws Exception {
        /*
        - Validate Access
        - Validate Databox
        - Validate userID
        - Update UID, Created and Modified
         */

        validateAccess(dataBox.getUserID());
        validateDatabox(dataBox);
        validateUserID(dataBox.getUserID());

        //Update UID, dates
        dataBox.setId(UUID.randomUUID().toString());
        dataBox.setCreated(LocalDate.now());
        dataBox.setModified(LocalDate.now());

        log.info("Adding databox {} to collection", dataBox.getName());
        DataBox addedDataBox = dataBoxRepo.addDataStore(dataBox);
        if (addedDataBox != null) {
            dataBoxPublisher.publishDataBoxAddedEvent(addedDataBox);
        }
        return addedDataBox;
    }

    public boolean updateDatabox(DataBox dataBox) throws Exception {
        validateAccess(dataBox.getUserID());
        validateDatabox(dataBox);
        validateUserID(dataBox.getUserID());

        //Update ID, and date
        dataBox.setId(UUID.randomUUID().toString());
        dataBox.setCreated(LocalDate.now());
        dataBox.setModified(LocalDate.now());

        log.info("Updating databox {} to collection", dataBox.getName());
        boolean updated = dataBoxRepo.updateDataStore(dataBox);
        if (updated) {
            cacheService.getDataBoxCache().clearDataBox(dataBox.getId());
            dataBoxPublisher.publishDataBoxUpdatedEvent(dataBox);
        }
        return updated;
    }

    public boolean deleteDatabox(String id) throws AccessDeniedException, FileNotFoundException {
        DataBox db = getDataboxAndValidateAccess(id);
        log.info("Deleting databox {} from collection", id);
        boolean deleted = dataBoxRepo.deleteDataBox(id);
        if (deleted) {
            cacheService.getDataBoxCache().clearDataBox(id);
            dataBoxPublisher.publishDataBoxDeletedEvent(db);
        }
        return deleted;
    }

    public DataBox getDatabox(String id) throws AccessDeniedException, FileNotFoundException {
        /*
        1. Get box from cache or db
        2. compare userID of box's userID against currentUser's ID
        3. Validate access
         */
        return getDataboxAndValidateAccess(id);
    }

    public List<DataBox> getDataboxesOfUser(String userId) throws AccessDeniedException {
        validateAccess(userId);
        log.info("Getting databox of user {} from collection", userId);
        return dataBoxRepo.getDataStoresByUserID(userId);
    }

    public List<DataBox> getAllDatabox(int skip, int limit) throws AccessDeniedException {
        validateAdminAccess();
        return dataBoxRepo.getAllDatastore(skip, limit);
    }

    private void validateAccess(String userId) throws AccessDeniedException {
        KDAPAuthenticated currentUser = securityHelper.getCurrentUser();
        if (!currentUser.getUser().getAccessLevel().equals(AccessLevelConst.ADMIN) &&
                !userId.equals(currentUser.getUser().getId())) {
            log.info("Access denied. Attempting to access data for userID {} while current user is {}", userId, currentUser.getUser().getId());
            throw new AccessDeniedException("Access denied");
        }
    }

    private void validateAdminAccess() throws AccessDeniedException {
        KDAPAuthenticated currentUser = securityHelper.getCurrentUser();
        if (!Objects.equals(currentUser.getUser().getAccessLevel(), AccessLevelConst.ADMIN)) {
            log.error("Access denied. Can't get all databoxes");
            throw new AccessDeniedException("Access denied. Can't get all databoxes");
        }
    }

    private void validateDatabox(DataBox dataBox) throws Exception {
        if (dataBox == null) throw new NullPointerException("Databox is null");
        if (dataBox.getFields() == null || dataBox.getFields().isEmpty())
            throw new Exception("Missing fields for " + dataBox);
        if (dataBox.getName() == null || dataBox.getName().isEmpty())
            throw new Exception("Missing name for databox " + dataBox);
    }

    private void validateUserID(String userId) throws Exception {
        ResponseEntity<ResponseModel<List<KDAPUser>>> authResp = authenticationComs.getUserInfoByID(userId, "Bearer " + serviceCommunicationHelper.generateToken());
        if (!authResp.getStatusCode().is2xxSuccessful()) {
            throw new Exception("Getting userinfo from authentication failed with message" + authResp.getBody().getMessage());
        }
        log.info("Got back response : "+ authResp.getBody().toString());
        if (authResp.getBody().getData() == null || authResp.getBody().getData().size() != 1) {
            throw new Exception("Expected one user info but got null or more than one or empty " + authResp.getBody());
        }
    }

    private DataBox getDataboxAndValidateAccess(String id) throws AccessDeniedException, FileNotFoundException {
        DataBox db = cacheService.getDataBoxCache().getDataBox(id);
        if (db == null) {
            db = dataBoxRepo.getDataBoxByID(id);
            if (db != null) {
                cacheService.getDataBoxCache().cacheDataBox(db);
            }
        }
        if (db == null) {
            throw new FileNotFoundException("Databox not found in database");
        }
        validateAccess(db.getUserID());
        return db;
    }
}