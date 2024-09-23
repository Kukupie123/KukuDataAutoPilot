package dev.kukukodes.kdap.authenticationservice.service;

import dev.kukukodes.kdap.authenticationservice.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CacheService {

    private final CacheManager cacheManager;
    private final String userCacheName = "user";

    public CacheService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void cacheUser(UserEntity userEntity) {
        log.info("Caching user {}", userEntity);
        cacheManager.getCache(userCacheName).put(userEntity.getId(), userEntity);
    }

    public UserEntity getUser(String id) {
        var user = cacheManager.getCache(userCacheName).get(id, UserEntity.class);
        if (user == null) {
            log.info("No user cached with key {}", id);
        }
        log.info("Returning cached user {}", id);
        return user;
    }

    public void removeUser(String id) {
        log.info("Removing user cached with key {}", id);
        cacheManager.getCache(userCacheName).evict(id);
    }
}
