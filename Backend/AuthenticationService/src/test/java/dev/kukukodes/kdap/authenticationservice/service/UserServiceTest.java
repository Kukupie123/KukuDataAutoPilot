package dev.kukukodes.kdap.authenticationservice.service;

import dev.kukukodes.kdap.authenticationservice.entity.UserEntity;
import dev.kukukodes.kdap.authenticationservice.enums.UserRole;
import dev.kukukodes.kdap.authenticationservice.helpers.SecurityHelper;
import dev.kukukodes.kdap.authenticationservice.models.KDAPUserAuthentication;
import dev.kukukodes.kdap.authenticationservice.publishers.UserEventPublisher;
import dev.kukukodes.kdap.authenticationservice.repo.IUserRepo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

class UserServiceTest {
    @Mock
    IUserRepo userRepo;
    @Mock
    CacheService cacheService;
    @Mock
    UserEventPublisher userEventPublisher;
    @Mock
    SecurityHelper securityHelper;
    UserService userService;
    private helper helper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        helper = new helper(userRepo, cacheService, userEventPublisher, securityHelper);
    }

    @AfterEach
    void tearDown() {
        userService = null;
    }

    /**
     * Expected to be able to create user as we have full access. UserRole is ignored
     */
    @Test
    void addUser_FullAccess_Admin() {
        var testUser = helper.createTestUser();
        helper.getSecurityHelper_helper().when_getKDAPUserAuthentication(securityHelper, UserRole.ADMIN);
        helper.getUserRepo_helper().when_userRepo_addUser(userRepo, testUser, testUser);
        userService = helper.createUserService(true);

        StepVerifier.create(userService.addUser(testUser))
                .assertNext(user -> {
                    Assertions.assertThat(user).isNotNull();
                    Assertions.assertThat(user.getId()).isNotNull();
                    Assertions.assertThat(user.getId()).isEqualTo(testUser.getId());
                    Assertions.assertThat(user.getName()).isEqualTo(testUser.getName());
                    Assertions.assertThat(user.getEmail()).isEqualTo(testUser.getEmail());
                    Assertions.assertThat(user.getPassword()).isEqualTo(testUser.getPassword());
                    Assertions.assertThat(user.getPicture()).isEqualTo(testUser.getPicture());
                }).verifyComplete();

    }

    /**
     * Expected to be able to create user as we have full access. UserRole is ignored
     */
    @Test
    void addUser_FullAccess_User() {
        var testUser = helper.createTestUser();
        helper.getSecurityHelper_helper().when_getKDAPUserAuthentication(securityHelper, UserRole.USER);
        helper.getUserRepo_helper().when_userRepo_addUser(userRepo, testUser, testUser);
        userService = helper.createUserService(true);
        StepVerifier.create(userService.addUser(testUser))
                .assertNext(user -> {
                    Assertions.assertThat(user).isNotNull();
                    Assertions.assertThat(user.getId()).isNotNull();
                    Assertions.assertThat(user.getId()).isEqualTo(testUser.getId());
                    Assertions.assertThat(user.getName()).isEqualTo(testUser.getName());
                    Assertions.assertThat(user.getEmail()).isEqualTo(testUser.getEmail());
                    Assertions.assertThat(user.getPassword()).isEqualTo(testUser.getPassword());
                    Assertions.assertThat(user.getPicture()).isEqualTo(testUser.getPicture());
                }).verifyComplete();
    }

    /**
     * Expected to be able to add users because role is Admin
     */
    @Test
    void addUser_NotFullAccess_Admin() {
        var testUser = helper.createTestUser();
        helper.getSecurityHelper_helper().when_getKDAPUserAuthentication(securityHelper, UserRole.ADMIN);
        helper.getUserRepo_helper().when_userRepo_addUser(userRepo, testUser, testUser);
        userService = helper.createUserService(false);

        StepVerifier.create(userService.addUser(testUser))
                .assertNext(user -> {
                    Assertions.assertThat(user).isNotNull();
                    Assertions.assertThat(user.getId()).isNotNull();
                    Assertions.assertThat(user.getId()).isEqualTo(testUser.getId());
                    Assertions.assertThat(user.getName()).isEqualTo(testUser.getName());
                    Assertions.assertThat(user.getEmail()).isEqualTo(testUser.getEmail());
                    Assertions.assertThat(user.getPassword()).isEqualTo(testUser.getPassword());
                    Assertions.assertThat(user.getPicture()).isEqualTo(testUser.getPicture());
                }).verifyComplete();
    }

    /**
     * Expected to not be able to add users as not admin and no full access
     */
    @Test
    void addUser_NotFullAccess_User() {
        var testUser = helper.createTestUser();
        helper.getSecurityHelper_helper().when_getKDAPUserAuthentication(securityHelper, UserRole.USER);
        helper.getUserRepo_helper().when_userRepo_addUser(userRepo, testUser, testUser);
        userService = helper.createUserService(false);

        StepVerifier.create(userService.addUser(testUser))
                .expectError(AccessDeniedException.class).verify();
    }
}


@RequiredArgsConstructor
class helper {
    final IUserRepo userRepo;
    final CacheService cacheService;
    final UserEventPublisher userEventPublisher;
    final SecurityHelper securityHelper;
    @Getter
    private final securityHelper_helper securityHelper_helper = new securityHelper_helper();
    @Getter
    private final userRepo_helper userRepo_helper = new userRepo_helper();
    @Getter
    private final userEventPublisher_helper userEventPublisher_helper = new userEventPublisher_helper();

    public UserService createUserService(boolean fullAccess) {
        return new UserService(userRepo, cacheService, securityHelper, userEventPublisher, fullAccess);
    }

    public UserEntity createTestUser() {
        return new UserEntity("1", "testName", "", LocalDate.now(), LocalDate.now(), "testMail", "pic");
    }


    class securityHelper_helper {
        public void when_getKDAPUserAuthentication(SecurityHelper securityHelper, UserRole role) {
            Mockito.when(securityHelper.getKDAPUserAuthentication()).thenReturn(Mono.just(new KDAPUserAuthentication("token", "1", role, true)));
        }
    }

    class userRepo_helper {
        public void when_userRepo_addUser(IUserRepo userRepo, UserEntity userParam, UserEntity userToReturn) {
            Mockito.when(userRepo.addUser(userParam)).thenReturn(Mono.just(userToReturn));
        }
    }

    class userEventPublisher_helper {
        public void when_userAdded(UserEventPublisher userEventPublisher, UserEntity userAdded) {
            Mockito.doAnswer(invocation -> {
                // Print something when the method is invoked
                System.out.println("publishUserUpdatedEvent called with: " + userAdded);
                return null; // Void method, so return null
            }).when(userEventPublisher).publishUserUpdatedEvent(userAdded);
        }


    }
}
