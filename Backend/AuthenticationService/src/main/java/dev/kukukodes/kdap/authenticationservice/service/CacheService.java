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

    public void cacheUser(KDAPUserEntity KDAPUserEntity) {
        log.info("Caching user {}", KDAPUserEntity);
        cacheManager.getCache(userCacheName).put(KDAPUserEntity.getId(), KDAPUserEntity);
    }

    public KDAPUserEntity getUser(String id) {
        try {
            var user = cacheManager.getCache(userCacheName).get(id, KDAPUserEntity.class);
            if (user == null) {
                log.info("No user cached with key {}", id);
            }
            log.info("Returning cached user {}", id);
            return user;
        } catch (IllegalStateException e) {
            cacheManager.getCache(userCacheName).evict(id);
            return null;
        }

    }

    public void removeUser(String id) {
        log.info("Removing user cached with key {}", id);
        cacheManager.getCache(userCacheName).evict(id);
    }
}
