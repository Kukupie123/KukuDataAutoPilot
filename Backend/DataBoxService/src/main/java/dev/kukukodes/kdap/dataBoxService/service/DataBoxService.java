package dev.kukukodes.kdap.dataBoxService.service;

import dev.kukukodes.kdap.dataBoxService.entity.dataBox.DataBox;
import dev.kukukodes.kdap.dataBoxService.enums.KDAPUserAuthority;
import dev.kukukodes.kdap.dataBoxService.helper.SecurityHelper;
import dev.kukukodes.kdap.dataBoxService.model.user.KDAPAuthenticatedUser;
import dev.kukukodes.kdap.dataBoxService.repo.IDataBoxRepo;
import dev.kukukodes.kdap.dataBoxService.publisher.DataBoxPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.nio.file.AccessDeniedException;
import java.util.List;

@Slf4j
@Service
public class DataBoxService {
    private final IDataBoxRepo dataBoxRepo;
    private final SecurityHelper securityHelper;
    private final CacheService cacheService;
    private final DataBoxPublisher dataBoxPublisher;

    public DataBoxService(IDataBoxRepo dataBoxRepo, SecurityHelper securityHelper, CacheService cacheService, DataBoxPublisher dataBoxPublisher) {
        this.dataBoxRepo = dataBoxRepo;
        this.securityHelper = securityHelper;
        this.cacheService = cacheService;
        this.dataBoxPublisher = dataBoxPublisher;
    }

    public DataBox addDatabox(DataBox dataBox) {
        KDAPUserAuthority authority = securityHelper.getCurrentUser().getUser().getAuthority();
        if (authority != KDAPUserAuthority.ADMIN) {
            if (dataBox.getUserID().equals(securityHelper.getCurrentUser().getUser().getId())) {
                log.info("Access denied. Attempting to add databox for userID {} while current user is {}", dataBox.getUserID(), securityHelper.getCurrentUser().getUser().getId());
                return null;
            }
        }
        log.info("Adding databox {} to collection", dataBox.getName());
        DataBox addedDataBox = dataBoxRepo.addDataStore(dataBox);
        if (addedDataBox != null) {
            dataBoxPublisher.publishDataBoxAddedEvent(addedDataBox);
        }
        return addedDataBox;
    }

    public boolean updateDatabox(DataBox dataBox) {
        KDAPAuthenticatedUser currentUser = securityHelper.getCurrentUser();
        if (currentUser.getUser().getAuthority() != KDAPUserAuthority.ADMIN) {
            if (!dataBox.getUserID().equals(currentUser.getUser().getId())) {
                log.info("Access denied. Attempted to update databox for user {} while current user is {}", dataBox.getUserID(), currentUser.getUser().getId());
                return false;
            }
        }
        log.info("Updating databox {} to collection", dataBox.getName());
        boolean updated = dataBoxRepo.updateDataStore(dataBox);
        if (updated) {
            cacheService.getDataBoxCache().clearDataBox(dataBox.getId());
            dataBoxPublisher.publishDataBoxUpdatedEvent(dataBox);
        }
        return updated;
    }

    public boolean deleteDatabox(String id) {
        DataBox db = dataBoxRepo.getDataBoxByID(id);
        if (db == null) {
            log.info("DataBox with id {} not found", id);
            return false;
        }
        if (!db.getUserID().equals(securityHelper.getCurrentUser().getUser().getId())) {
            if (securityHelper.getCurrentUser().getUser().getAuthority() != KDAPUserAuthority.ADMIN) {
                log.info("Access denied. Can't delete databox with id {} as it belongs to user {} but logged in as {}", id, db.getUserID(), securityHelper.getCurrentUser().getUser().getId());
                return false;
            }
        }
        log.info("Deleting databox {} from collection", id);
        boolean deleted = dataBoxRepo.deleteDataBox(id);
        if (deleted) {
            cacheService.getDataBoxCache().clearDataBox(id);
            dataBoxPublisher.publishDataBoxDeletedEvent(db);
        }
        return deleted;
    }

    public DataBox getDatabox(String id) throws AccessDeniedException, FileNotFoundException {
        var currentUser = securityHelper.getCurrentUser();
        DataBox db = cacheService.getDataBoxCache().getDataBox(id);
        if (db == null) {
            db = dataBoxRepo.getDataBoxByID(id);
            cacheService.getDataBoxCache().cacheDataBox(db);
        }
        if (db == null) {
            throw new FileNotFoundException("Databox not found in database");
        }
        if (!db.getUserID().equals(currentUser.getUser().getId())) {
            if (securityHelper.getCurrentUser().getUser().getAuthority() != KDAPUserAuthority.ADMIN) {
                log.info("Access denied. Can't get databox with id {} as it belongs to user {} but logged in as {}", id, db.getUserID(), securityHelper.getCurrentUser().getUser().getId());
                throw new AccessDeniedException(String.format("Logged in as %s but attempted to access databox of user %s without admin role", currentUser.getUser().getId(), db.getUserID()));
            }
        }
        log.info("Getting databox {} from collection", id);
        return db;
    }

    public List<DataBox> getDataboxOfUser(String userId) {
        KDAPAuthenticatedUser currentUser = securityHelper.getCurrentUser();
        if (currentUser.getUser().getAuthority() != KDAPUserAuthority.ADMIN) {
            if (!userId.equals(currentUser.getUser().getId())) {
                log.info("Access denied. Can't get data boxes of user {} because logged in as {}", userId, currentUser.getUser().getId());
                return List.of();
            }
        }
        log.info("Getting databox of user {} from collection", userId);
        return dataBoxRepo.getDataStoresByUserID(userId);
    }
}
