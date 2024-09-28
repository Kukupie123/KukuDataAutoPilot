package dev.kukukodes.kdap.dataBoxService.service;

import dev.kukukodes.kdap.dataBoxService.entity.dataBox.DataBox;
import dev.kukukodes.kdap.dataBoxService.entity.dataEntry.DataEntry;
import dev.kukukodes.kdap.dataBoxService.helper.ExceptionHelper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Slf4j
@Service

public class CacheService {
    private final CacheManager cacheManager;
    private final String dataBoxCacheName = "dataBoxCache";
    private final String dataEntryCacheName = "dataEntryCache";
    @Getter
    private final DataBoxCache dataBoxCache;
    @Getter
    private final DataEntryCache dataEntryCache;
    private final ExceptionHelper exceptionHelper;

    public CacheService(CacheManager cacheManager, ExceptionHelper exceptionHelper) {
        this.cacheManager = cacheManager;
        this.exceptionHelper = exceptionHelper;
        dataBoxCache = new DataBoxCache();
        dataEntryCache = new DataEntryCache();
    }

    protected void cacheObj(String key, Object object, String cacheName) {
        try {
            log.info("Caching {} with key {} in cache {}", cacheName, key, cacheName);
            cacheManager.getCache(cacheName).put(key, object);
        } catch (Exception e) {
            exceptionHelper.logException(log, e);
        }
    }

    protected void clearCache(String cacheName, String key) {
        try {
            log.info("Clearing cache {} key {}", cacheName, key);
            cacheManager.getCache(cacheName).evict(key);
        } catch (Exception e) {
            exceptionHelper.logException(log, e);
        }
    }

    protected <T> T getObj(String key, Class<T> cacheObjType, String cacheName) {
        try {
            log.info("Attempting to get cached {} of key {} from {}", cacheObjType.getName(), key, cacheName);
            T obj = cacheManager.getCache(cacheName).get(key, cacheObjType);
            if (obj == null) {
                throw new NullPointerException(String.format("Failed to get object from cache in %s, key %s", cacheName, key));
            }
            log.info("Got {} from key {} in {}", obj, key, cacheName);
            return obj;
        } catch (Exception e) {
            exceptionHelper.logException(log, e);
            clearCache(cacheName, key);
            return null;
        }
    }

    class DataBoxCache {
        public void cacheDataBox(DataBox dataBox) {
            cacheObj(dataBox.getId(), dataBox, dataBoxCacheName);
        }

        public DataBox getDataBox(String id) {
            return getObj(id, DataBox.class, dataBoxCacheName);
        }

        public void clearDataBox(String id) {
            clearCache(id, dataBoxCacheName);
        }

    }

    class DataEntryCache {
        public void cacheDataEntry(DataEntry dataEntry) {
            cacheObj(dataEntry.getId(), dataEntry, dataEntryCacheName);
        }

        public void clearDataEntry(String id) {
            clearCache(dataEntryCacheName, id);
        }

        public DataEntry getDataEntry(String id) {
            return getObj(id, DataEntry.class, dataEntryCacheName);
        }
    }
}
