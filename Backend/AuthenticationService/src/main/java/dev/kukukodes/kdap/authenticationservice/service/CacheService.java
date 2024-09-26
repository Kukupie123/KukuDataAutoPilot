package dev.kukukodes.kdap.authenticationservice.service;

import dev.kukukodes.kdap.authenticationservice.entity.user.KDAPUserEntity;
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

    public void cacheUser(KDAPUserEntity user) {
        if (user == null) {
            log.error("Cant cache null user");
            return;
        }
        log.info("Caching user {}", user);
        cacheManager.getCache(userCacheName).put(user.getId(), user);
    }

    public KDAPUserEntity getUser(String id) {
        try {
            var user = cacheManager.getCache(userCacheName).get(id, KDAPUserEntity.class);
            if (user == null || user.getAccessLevel() == null) {
                log.info("No user cached with key {} or invalid access level", id);
                return null;
            }
            log.info("Returning cached user {}", user);
            return user;
        } catch (Exception e) {
            log.error("Error when getting cached user because {}: {}", e.getMessage(), e.getCause().toString());
            cacheManager.getCache(userCacheName).evict(id);
            return null;
        }


    }

    public void removeUser(String id) {
        log.info("Removing user cached with key {}", id);
        cacheManager.getCache(userCacheName).evict(id);
    }
}
