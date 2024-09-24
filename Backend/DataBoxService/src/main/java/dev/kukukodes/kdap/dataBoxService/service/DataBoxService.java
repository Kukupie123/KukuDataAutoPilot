package dev.kukukodes.kdap.dataBoxService.service;

import dev.kukukodes.kdap.dataBoxService.entity.dataBox.DataBox;
import dev.kukukodes.kdap.dataBoxService.enums.KDAPUserAuthority;
import dev.kukukodes.kdap.dataBoxService.helper.SecurityHelper;
import dev.kukukodes.kdap.dataBoxService.model.KDAPAuthenticatedUser;
import dev.kukukodes.kdap.dataBoxService.repo.IDataBoxRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DataBoxService {
    private final IDataBoxRepo dataBoxRepo;
    private final SecurityHelper securityHelper;
    private final CacheService cacheService;

    public DataBoxService(IDataBoxRepo dataBoxRepo, SecurityHelper securityHelper, CacheService cacheService) {
        this.dataBoxRepo = dataBoxRepo;
        this.securityHelper = securityHelper;
        this.cacheService = cacheService;
    }

    /**
     * Add the databox for user. Checks current user's authority
     */
    public DataBox addDatabox(DataBox dataBox) {
        KDAPUserAuthority authority = securityHelper.getCurrentUser().getUser().getAuthority();
        if (authority != KDAPUserAuthority.ADMIN) {
            if (dataBox.getUserID().equals(securityHelper.getCurrentUser().getUser().getId())) {
                log.info("Access denied. Attempting to add databox for userID {} while current user is {}", dataBox.getUserID(), securityHelper.getCurrentUser().getUser().getId());
                return null;
            }
        }
        log.info("Adding databox {} to collection", dataBox.getName());
        return dataBoxRepo.addDataStore(dataBox);
        //TODO: Publish event
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
        var updated = dataBoxRepo.updateDataStore(dataBox);
        cacheService.clearDataBox(dataBox.getId());
        return updated;
        //TODO: Publish event
    }

    public boolean deleteDatabox(String id) {
        //Get databox and verify its user
        var db = dataBoxRepo.getDataBoxByID(id);
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
        var deleted = dataBoxRepo.deleteDataBox(id);
        if (deleted) {
            cacheService.clearDataBox(id);
            //TODO Publish event
        }
        return deleted;
    }

    public DataBox getDatabox(String id) {
        var db = cacheService.getDataBox(id);
        if (db == null) {
            db = dataBoxRepo.getDataBoxByID(id);
            cacheService.cacheDataBox(db);
        }
        if (db == null) {
            log.info("DataBox with id {} not found", id);
            return null;
        }
        if (!db.getUserID().equals(securityHelper.getCurrentUser().getUser().getId())) {
            if (securityHelper.getCurrentUser().getUser().getAuthority() != KDAPUserAuthority.ADMIN) {
                log.info("Access denied. Can't get databox with id {} as it belongs to user {} but logged in as {}", id, db.getUserID(), securityHelper.getCurrentUser().getUser().getId());
                return null;
            }
        }
        log.info("Getting databox {} from collection", id);
        return db;
    }

    //Not caching list. You never know how big they might be
    public List<DataBox> getDataboxOfUser(String userId) {
        KDAPAuthenticatedUser currentUser = securityHelper.getCurrentUser();
        if(currentUser.getUser().getAuthority() != KDAPUserAuthority.ADMIN) {
            if(!userId.equals(currentUser.getUser().getId())) {
                log.info("Access denied. Can't get data boxes of user {} because logged in as {}", userId,currentUser.getUser().getId());
                return List.of();
            }
        }
        log.info("Getting databox of user {} from collection", userId);
        return dataBoxRepo.getDataStoresByUserID(userId);
    }

}
