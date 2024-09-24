package dev.kukukodes.kdap.authenticationservice.service;

import dev.kukukodes.kdap.authenticationservice.entity.user.KDAPUserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CacheServiceTest {

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache userCache;

    @InjectMocks
    private CacheService cacheService;

    private static final String USER_CACHE_NAME = "user";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(cacheManager.getCache(USER_CACHE_NAME)).thenReturn(userCache);
    }

    @Test
    void cacheUser_shouldCacheUserCorrectly() {
        // Arrange
        KDAPUserEntity KDAPUserEntity = new KDAPUserEntity();
        KDAPUserEntity.setId("123");

        // Act
        cacheService.cacheUser(KDAPUserEntity);

        // Assert
        verify(userCache).put(eq(KDAPUserEntity.getId()), eq(KDAPUserEntity));
    }

    @Test
    void getUser_shouldReturnCachedUserWhenExists() {
        // Arrange
        String userId = "123";
        KDAPUserEntity cachedUser = new KDAPUserEntity();
        cachedUser.setId(userId);
        when(userCache.get(userId, KDAPUserEntity.class)).thenReturn(cachedUser);

        // Act
        KDAPUserEntity result = cacheService.getUser(userId);

        // Assert
        verify(userCache).get(eq(userId), eq(KDAPUserEntity.class));
        assertThat(result).isEqualTo(cachedUser);
    }

    @Test
    void getUser_shouldReturnNullWhenUserNotCached() {
        // Arrange
        String userId = "123";
        when(userCache.get(userId, KDAPUserEntity.class)).thenReturn(null);

        // Act
        KDAPUserEntity result = cacheService.getUser(userId);

        // Assert
        verify(userCache).get(eq(userId), eq(KDAPUserEntity.class));
        assertThat(result).isNull();
    }

    @Test
    void removeUser_shouldEvictUserFromCache() {
        // Arrange
        String userId = "123";

        // Act
        cacheService.removeUser(userId);

        // Assert
        verify(userCache).evict(eq(userId));
    }

    @Test
    void cacheUser_shouldLogCaching() {
        // Arrange
        KDAPUserEntity KDAPUserEntity = new KDAPUserEntity();
        KDAPUserEntity.setId("123");

        // Act
        cacheService.cacheUser(KDAPUserEntity);

        // Assert
        ArgumentCaptor<KDAPUserEntity> captor = ArgumentCaptor.forClass(KDAPUserEntity.class);
        verify(userCache).put(eq("123"), captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo("123");
    }

    @Test
    void getUser_shouldLogWhenNoUserInCache() {
        // Arrange
        String userId = "456";
        when(userCache.get(userId, KDAPUserEntity.class)).thenReturn(null);

        // Act
        KDAPUserEntity result = cacheService.getUser(userId);

        // Assert
        assertThat(result).isNull();
        verify(userCache).get(eq(userId), eq(KDAPUserEntity.class));
    }
}
