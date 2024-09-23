package dev.kukukodes.kdap.dataBoxService.service;

import dev.kukukodes.kdap.dataBoxService.entity.dataBox.DataBox;
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

    public DataBoxService(IDataBoxRepo dataBoxRepo) {
        this.dataBoxRepo = dataBoxRepo;
    }
    public DataBox addDatabox(DataBox dataBox) {
        log.info("Adding databox {} to collection", dataBox.getName());
        return dataBoxRepo.addDataStore(dataBox);
    }

    @CacheEvict(value = "databox", key = "#dataBox.id")
    public boolean updateDatabox(DataBox dataBox) {
        log.info("Updating databox {} to collection", dataBox.getName());
        return dataBoxRepo.updateDataStore(dataBox);
    }

    @CacheEvict(value = "databox", key = "#id")
    public boolean deleteDatabox(String id) {
        log.info("Deleting databox {} from collection", id);
        return dataBoxRepo.deleteDataBox(id);
    }

    @Cacheable(value = "databox", key = "#id")
    public DataBox getDatabox(String id) {
        log.info("Getting databox {} from collection", id);
        return dataBoxRepo.getDataBoxByID(id);
    }

    //Not caching list. You never know how big they might be
    public List<DataBox> getDataboxOfUser(String userId) {
        log.info("Getting databox of user {} from collection", userId);
        return dataBoxRepo.getDataStoresByUserID(userId);
    }

}
