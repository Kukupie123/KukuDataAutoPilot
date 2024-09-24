package dev.kukukodes.kdap.dataBoxService.service;

import dev.kukukodes.kdap.dataBoxService.entity.dataBox.DataBox;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CacheService {
    private final CacheManager cacheManager;
    private final String dataBoxCacheName = "dataBoxCache";
    @Getter
    private final DataBoxCache dataBoxCache;

    public CacheService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
        dataBoxCache = new DataBoxCache();
    }

    class DataBoxCache {
        public void cacheDataBox(DataBox dataBox) {
            if(dataBox == null) {
                log.warn("DataBox is null skipping caching");
                return;
            }
            log.info("Caching databox {}", dataBox);
            cacheManager.getCache(dataBoxCacheName).put(dataBox.getId(), dataBox);
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

    class DataEntryCache{

    }
}
