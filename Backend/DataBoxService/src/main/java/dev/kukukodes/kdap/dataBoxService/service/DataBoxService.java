package dev.kukukodes.kdap.dataBoxService.service;

import dev.kukukodes.kdap.dataBoxService.constants.AccessLevelConst;
import dev.kukukodes.kdap.dataBoxService.entity.dataBox.DataBox;
import dev.kukukodes.kdap.dataBoxService.helper.DataboxHelper;
import dev.kukukodes.kdap.dataBoxService.helper.SecurityHelper;
import dev.kukukodes.kdap.dataBoxService.helper.ServiceCommunicationHelper;
import dev.kukukodes.kdap.dataBoxService.model.user.KDAPAuthenticated;
import dev.kukukodes.kdap.dataBoxService.openFeign.AuthenticationComs;
import dev.kukukodes.kdap.dataBoxService.publisher.DataBoxPublisher;
import dev.kukukodes.kdap.dataBoxService.repo.IDataBoxRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final DataboxHelper databoxHelper;

    public DataBox addDatabox(DataBox dataBox) throws Exception {
        /*
        - Validate Access
        - Validate Databox
        - Validate userID
        - Update UID, Created and Modified
         */
        securityHelper.validateAccess(dataBox.getUserID());
        databoxHelper.validateDatabox(dataBox);
        databoxHelper.validateUserID(dataBox.getUserID());

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
        securityHelper.validateAccess(dataBox.getUserID());
        databoxHelper.validateDatabox(dataBox);
        databoxHelper.validateUserID(dataBox.getUserID());

        //Update ID, and date
        log.info("Updating databox {} in collection", dataBox.getName());
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
        return getDataboxAndValidateAccess(id);
    }

    public List<DataBox> getDataboxesOfUser(String userId) throws AccessDeniedException {
        securityHelper.validateAccess(userId);
        log.info("Getting databox of user {} from collection", userId);
        return dataBoxRepo.getDataStoresByUserID(userId);
    }

    public List<DataBox> getAllDatabox(int skip, int limit) throws AccessDeniedException {
        validateAdminAccess();
        return dataBoxRepo.getAllDatastore(skip, limit);
    }


    private void validateAdminAccess() throws AccessDeniedException {
        KDAPAuthenticated currentUser = securityHelper.getCurrentUser();
        if (!Objects.equals(currentUser.getUser().getAccessLevel(), AccessLevelConst.ADMIN)) {
            log.error("Access denied. Can't get all databoxes");
            throw new AccessDeniedException("Access denied. Can't get all databoxes");
        }
    }


    private DataBox getDataboxAndValidateAccess(String id) throws AccessDeniedException, FileNotFoundException {
        /*
        1. Get box from cache or db
        2. compare userID of box's userID against currentUser's ID
        3. Validate access
         */
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
        securityHelper.validateAccess(db.getUserID());
        return db;
    }
}