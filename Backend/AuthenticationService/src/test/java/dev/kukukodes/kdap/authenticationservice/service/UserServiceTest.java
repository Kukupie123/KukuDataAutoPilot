package dev.kukukodes.kdap.authenticationservice.service;

import dev.kukukodes.kdap.authenticationservice.entity.user.KDAPUserEntity;
import dev.kukukodes.kdap.authenticationservice.helpers.SecurityHelper;
import dev.kukukodes.kdap.authenticationservice.publishers.UserEventPublisher;
import dev.kukukodes.kdap.authenticationservice.repo.IUserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private IUserRepo userRepo;

    @Mock
    private CacheService cacheService;

    @Mock
    private SecurityHelper securityHelper;

    @Mock
    private UserEventPublisher userEventPublisher;

    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepo, cacheService, securityHelper, userEventPublisher, false);
    }

    @Test
    void addUser_AdminRole_Success() {
        // Arrange
        KDAPUserEntity userToAdd = new KDAPUserEntity();
        userToAdd.setId("user1");

        KDAPUserAuthentication adminAuth = new KDAPUserAuthentication("token","user1", AuthAccessLevel.ADMIN, true);

        when(securityHelper.getKDAPAuthenticated()).thenReturn(Mono.just(adminAuth));
        when(userRepo.addUser(userToAdd)).thenReturn(Mono.just(userToAdd));

        // Act
        Mono<KDAPUserEntity> result = userService.addUser(userToAdd);

        // Assert
        StepVerifier.create(result)
                .expectNext(userToAdd)
                .verifyComplete();

        verify(userRepo, times(1)).addUser(userToAdd);
        verify(userEventPublisher, times(1)).publishUserAddedEvent(userToAdd);
    }

    @Test
    void addUser_NonAdminRole_AccessDenied() {
        // Arrange
        KDAPUserEntity userToAdd = new KDAPUserEntity();
        KDAPUserAuthentication nonAdminAuth = new KDAPUserAuthentication("token","user2", AuthAccessLevel.USER, true);

        when(securityHelper.getKDAPAuthenticated()).thenReturn(Mono.just(nonAdminAuth));

        // Act
        Mono<KDAPUserEntity> result = userService.addUser(userToAdd);

        // Assert
        StepVerifier.create(result)
                .expectError(AccessDeniedException.class)
                .verify();

        verify(userRepo, never()).addUser(any(KDAPUserEntity.class));
        verify(userEventPublisher, never()).publishUserAddedEvent(any());
    }

    @Test
    void updateUser_SameUser_Success() {
        // Arrange
        KDAPUserEntity existingUser = new KDAPUserEntity();
        existingUser.setId("user1");
        existingUser.setEmail("oldEmail");

        KDAPUserEntity updatedUser = new KDAPUserEntity();
        updatedUser.setId("user1");
        updatedUser.setEmail("newEmail");

        KDAPUserAuthentication auth = new KDAPUserAuthentication("token","user1", AuthAccessLevel.USER, true);

        when(securityHelper.getKDAPAuthenticated()).thenReturn(Mono.just(auth));
        when(userRepo.getUserByID("user1")).thenReturn(Mono.just(existingUser));
        when(userRepo.updateUser(any(KDAPUserEntity.class))).thenReturn(Mono.just(updatedUser));

        // Act
        Mono<KDAPUserEntity> result = userService.updateUser(updatedUser);

        // Assert
        StepVerifier.create(result)
                .expectNext(updatedUser)
                .verifyComplete();

        verify(userRepo, times(1)).updateUser(updatedUser);
        verify(cacheService, times(1)).removeUser("user1");
        verify(userEventPublisher, times(1)).publishUserUpdatedEvent(updatedUser);
    }

    @Test
    void updateUser_OtherUser_AccessDenied() {
        // Arrange
        KDAPUserEntity existingUser = new KDAPUserEntity();
        existingUser.setId("user2");

        KDAPUserEntity updatedUser = new KDAPUserEntity();
        updatedUser.setId("user1");

        KDAPUserAuthentication auth = new KDAPUserAuthentication("token","user2", AuthAccessLevel.USER, true);

        when(securityHelper.getKDAPAuthenticated()).thenReturn(Mono.just(auth));

        // Act
        Mono<KDAPUserEntity> result = userService.updateUser(updatedUser);

        // Assert
        StepVerifier.create(result)
                .expectError(AccessDeniedException.class)
                .verify();

        verify(userRepo, never()).updateUser(any(KDAPUserEntity.class));
        verify(cacheService, never()).removeUser(anyString());
        verify(userEventPublisher, never()).publishUserUpdatedEvent(any());
    }

    @Test
    void getUserById_SuccessFromCache() {
        // Arrange
        KDAPUserEntity cachedUser = new KDAPUserEntity();
        cachedUser.setId("user1");

        KDAPUserAuthentication auth = new KDAPUserAuthentication("token","user1", AuthAccessLevel.USER, true);

        when(securityHelper.getKDAPAuthenticated()).thenReturn(Mono.just(auth));
        when(cacheService.getUser("user1")).thenReturn(cachedUser);

        // Act
        Mono<KDAPUserEntity> result = userService.getUserById("user1");

        // Assert
        StepVerifier.create(result)
                .expectNext(cachedUser)
                .verifyComplete();

        verify(userRepo, never()).getUserByID(anyString());
        verify(cacheService, never()).cacheUser(any());
    }

    @Test
    void deleteUser_AdminRole_Success() {
        // Arrange
        String userId = "user1";
        KDAPUserAuthentication adminAuth = new KDAPUserAuthentication("token","admin", AuthAccessLevel.ADMIN, true);

        when(securityHelper.getKDAPAuthenticated()).thenReturn(Mono.just(adminAuth));
        when(userRepo.deleteUserByID(userId)).thenReturn(Mono.just(true));

        // Act
        Mono<Boolean> result = userService.deleteUser(userId);

        // Assert
        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();

        verify(userRepo, times(1)).deleteUserByID(userId);
        verify(cacheService, times(1)).removeUser(userId);
        verify(userEventPublisher, times(1)).publishUserDeletedEvent(userId);
    }
}
