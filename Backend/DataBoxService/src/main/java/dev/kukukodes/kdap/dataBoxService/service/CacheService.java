package dev.kukukodes.kdap.dataBoxService.service;

import dev.kukukodes.kdap.dataBoxService.entity.dataBox.DataBox;
import dev.kukukodes.kdap.dataBoxService.helper.DataboxHelper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Slf4j
@Service

public class CacheService {
    private final CacheManager cacheManager;
    private final String dataBoxCacheName = "dataBoxCache";
    private final String dataEntryCacheName = "dataEntryCache";
    private final DataboxHelper databoxHelper;
    @Getter
    private final DataBoxCache dataBoxCache;
    @Getter
    private final DataEntryCache dataEntryCache;

    public CacheService(CacheManager cacheManager, DataboxHelper databoxHelper) {
        this.cacheManager = cacheManager;
        this.databoxHelper = databoxHelper;
        dataBoxCache = new DataBoxCache();
        dataEntryCache = new DataEntryCache();
    }

    class DataBoxCache {
        public void cacheDataBox(DataBox dataBox) {
            try {
                databoxHelper.validateDataboxValues(dataBox);
                log.info("Caching databox {}", dataBox);
                cacheManager.getCache(dataBoxCacheName).put(dataBox.getId(), dataBox);
            } catch (Exception e) {
                log.error("{}\n{}", e.getMessage(), Arrays.toString(e.getStackTrace()));
            }

        }

        public DataBox getDataBox(String id) {
            var db = cacheManager.getCache(dataBoxCacheName).get(id, DataBox.class);
            if (db == null) {
                log.info("No data box cached with id {}", id);
                return null;
            }
            log.info("Found cached data box {}", db);
            return db;
        }

        public void clearDataBox(String id) {
            log.info("Clearing cached data box {}", id);
            cacheManager.getCache(dataBoxCacheName).evict(id);
        }

    }

    class DataEntryCache {

    }
}
