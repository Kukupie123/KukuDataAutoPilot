package dev.kukukodes.kdap.dataBoxService.service;

import dev.kukukodes.kdap.dataBoxService.constants.AccessLevelConst;
import dev.kukukodes.kdap.dataBoxService.entity.dataBox.DataBox;
import dev.kukukodes.kdap.dataBoxService.helper.DataboxHelper;
import dev.kukukodes.kdap.dataBoxService.helper.RequestHelper;
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
    private final RequestHelper requestHelper;

    public DataBox addDatabox(DataBox dataBox) throws Exception {
        //Validate access
        securityHelper.validateAccess(dataBox.getUserID());
        //Validate databox
        databoxHelper.validateDataboxValues(dataBox);
        //Validate user
        var user = requestHelper.getAuthenticationRequest().getUserInfo(dataBox.getUserID());
        if (user == null) {
            throw new Exception("User is null");
        }
        //Update UID, Created and modified
        dataBox.setId(UUID.randomUUID().toString());
        dataBox.setCreated(LocalDate.now());
        dataBox.setModified(LocalDate.now());
        //Add to collection
        log.info("Adding databox {} to collection", dataBox.getName());
        DataBox addedDataBox = dataBoxRepo.addDataStore(dataBox);
        if (addedDataBox != null) {
            //Publish event if added
            dataBoxPublisher.publishDataBoxAddedEvent(addedDataBox);
        }
        return addedDataBox;
    }

    public boolean updateDatabox(DataBox dataBox) throws Exception {
        //Validate access
        securityHelper.validateAccess(dataBox.getUserID());
        //Validate databoxID
        if (dataBox.getId() == null) {
            throw new NullPointerException("DataBox id is null");
        }
        //Validate user
        var user = requestHelper.getAuthenticationRequest().getUserInfo(dataBox.getUserID());
        if (user == null) {
            throw new Exception("User is null");
        }
        //Compare against existing record
        var dbFromCollection = getDatabox(dataBox.getId());
        if (dbFromCollection == null) {
            throw new Exception("Databox not found with id " + dataBox.getId());
        }
        if (databoxHelper.compareDataboxes(dbFromCollection, dataBox)) {
            throw new Exception("Nothing to update");
        }
        //Update Modified and make sure created stays the same.
        dataBox.setModified(LocalDate.now());
        dataBox.setCreated(dbFromCollection.getCreated());
        dataBox.setUserID(dbFromCollection.getUserID());
        databoxHelper.updateDataboxFrom(dbFromCollection, dataBox);
        //Add to collection
        log.info("Updating databox {} in collection", dataBox.getName());
        boolean updated = dataBoxRepo.updateDataStore(dataBox);
        if (updated) {
            //Invalidate cache and publish event
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
        //Get db from cache
        DataBox db = cacheService.getDataBoxCache().getDataBox(id);
        if (db == null) {
            //Get db from collection
            db = dataBoxRepo.getDataBoxByID(id);
            if (db != null) {
                //Cache the retrieved db
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