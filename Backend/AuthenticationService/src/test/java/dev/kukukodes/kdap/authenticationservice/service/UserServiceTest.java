package dev.kukukodes.kdap.authenticationservice.service;

import dev.kukukodes.kdap.authenticationservice.constants.AccessLevelConst;
import dev.kukukodes.kdap.authenticationservice.constants.RequestSourceConst;
import dev.kukukodes.kdap.authenticationservice.entity.user.KDAPUserEntity;
import dev.kukukodes.kdap.authenticationservice.helpers.SecurityHelper;
import dev.kukukodes.kdap.authenticationservice.models.authentication.KDAPAuthenticated;
import dev.kukukodes.kdap.authenticationservice.publishers.UserEventPublisher;
import dev.kukukodes.kdap.authenticationservice.repo.IUserRepo;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@ExtendWith(MockitoExtension.class) //For mocks
class UserServiceTest {

    UserService userService;

    @Mock
    IUserRepo userRepo;
    @Mock
    CacheService cacheService;
    @Mock
    SecurityHelper securityHelper;
    @Mock
    UserEventPublisher userEventPublisher;

    @Nested
    class NonAdminUserTests {

        KDAPUserEntity currentUser = new KDAPUserEntity("kdap",
                "testName",
                "testPassword",
                LocalDate.now(),
                LocalDate.now(),
                "testEmail",
                "pic");
        KDAPUserEntity anotherUser = new KDAPUserEntity("another",
                "testName",
                "testPassword",
                LocalDate.now(),
                LocalDate.now(),
                "testEmail",
                "pic");

        KDAPAuthenticated kdapAuthenticated = new KDAPAuthenticated(
                AccessLevelConst.SELF,
                RequestSourceConst.CLIENT,
                currentUser
        );

        @BeforeEach
        void setUp() {
            userService = new UserService(userRepo, cacheService, securityHelper, userEventPublisher, false);
            Mockito.when(securityHelper.getKDAPAuthenticated()).thenReturn(Mono.just(kdapAuthenticated));
        }

        @Test
        void addUser_shouldDeny() {

            // Act & Assert: Check that AccessDeniedException is thrown
            Assertions.assertThatThrownBy(() -> userService.addUser(anotherUser).block())
                    .isInstanceOf(AccessDeniedException.class);
        }

        @Test
        void updateUser_Self_ShouldAllow() {
            var toUpdate = new KDAPUserEntity("kdap", "updatedName", "testPassword", LocalDate.now(), LocalDate.now(), "testEmail", "pic");

            Mockito.when(userRepo.getUserByID(currentUser.getId())).thenReturn(Mono.just(currentUser));
            Mockito.when(userRepo.updateUser(toUpdate)).thenReturn(Mono.just(toUpdate));
            var updated = userService.updateUser(toUpdate).block();
            Assertions.assertThat(updated).isEqualTo(toUpdate);

        }

        @Test
        void updateUser_Other_ShouldDeny() {
            Assertions.assertThatThrownBy(() -> userService.updateUser(anotherUser).block()).isInstanceOf(AccessDeniedException.class);
        }

        @Test
        void getUser_self_shouldAllow_fromCache() {
            var currentUser = kdapAuthenticated.getUser();
            Mockito.when(userRepo.getUserByID(currentUser.getId())).thenReturn(Mono.just(currentUser));
            Mockito.when(cacheService.getUser(currentUser.getId())).thenReturn(currentUser);
            var gotten = userService.getUserById(currentUser.getId()).block();
            Assertions.assertThat(gotten).isEqualTo(currentUser);
        }

        @Test
        void getUser_self_shouldAllow_fromDB() {
            var currentUser = kdapAuthenticated.getUser();
            Mockito.when(userRepo.getUserByID(currentUser.getId())).thenReturn(Mono.just(currentUser));
            Mockito.when(cacheService.getUser(currentUser.getId())).thenReturn(null);
            Mockito.when(userRepo.getUserByID(currentUser.getId())).thenReturn(Mono.just(currentUser));
            var gotten = userService.getUserById(currentUser.getId()).block();
            Assertions.assertThat(gotten).isEqualTo(currentUser);
        }

        @Test
        void getUser_other_shouldDeny_fromCache() {
            // Arrange
            Mockito.when(userRepo.getUserByID(anotherUser.getId())).thenReturn(Mono.just(anotherUser));

            Assertions.assertThatThrownBy(() -> userService.getUserById(anotherUser.getId()).block()).isInstanceOf(AccessDeniedException.class);
        }

        @Test
        void getUser_other_shouldDeny_fromDB() {
            // Arrange
            var currentUser = kdapAuthenticated.getUser();
            //No need to mock cache as it will get skipped. It's a flat map
            Mockito.when(userRepo.getUserByID(anotherUser.getId())).thenReturn(Mono.just(anotherUser));

            Assertions.assertThatThrownBy(() -> userService.getUserById(anotherUser.getId()).block()).isInstanceOf(AccessDeniedException.class);
        }

        @Test
        void deleteUser_self_shouldAllow() {
            var currentUser = kdapAuthenticated.getUser();
            Mockito.doNothing().when(cacheService).removeUser(currentUser.getId());
            Mockito.when(userRepo.deleteUserByID(currentUser.getId())).thenReturn(Mono.just(true));
            var success = userService.deleteUser(currentUser.getId()).block();
            Assertions.assertThat(success).isTrue();
        }

        @Test
        void deleteUser_other_shouldDeny() {
            Assertions.assertThatThrownBy(() -> userService.deleteUser(anotherUser.getId()).block()).isInstanceOf(AccessDeniedException.class);
        }
    }
}
