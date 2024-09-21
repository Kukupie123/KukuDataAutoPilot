package dev.kukukodes.kdap.authenticationservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.kukukodes.kdap.authenticationservice.entity.UserEntity;
import dev.kukukodes.kdap.authenticationservice.models.OAuth2UserInfoGoogle;
import dev.kukukodes.kdap.authenticationservice.publishers.UserEventPublisher;
import dev.kukukodes.kdap.authenticationservice.repo.IUserRepo;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {
    @Mock
    private IUserRepo userRepo;
    @Mock
    private UserEventPublisher userEventPublisher;
    @InjectMocks
    private UserService userService;

    private UserEntity testUser = new UserEntity("testID", "testName", "testPassword", LocalDate.now(), LocalDate.now(), "testEmail", "testPic");

    @Test
    void addUser_shouldCallRepoAndReturnUser() {
        Mockito.when(userRepo.addUser(testUser)).thenReturn(Mono.just(testUser));
        StepVerifier.create(userService.addUser(testUser))
                .expectNext(testUser)
                .verifyComplete();

    }

    @Test
    void updateUser() throws JsonProcessingException {
        Mockito.doNothing().when(userEventPublisher).publishUserUpdateMsg(testUser);
        Mockito.when(userRepo.updateUser(testUser)).thenReturn(Mono.just(testUser));

        StepVerifier.create(userService.updateUser(testUser))
                .expectNext(testUser)
                .verifyComplete();
    }

    @Test
    void getUserByID_shouldGetTestUser() {
        Mockito.when(userRepo.getUserByID(testUser.getId())).thenReturn(Mono.just(testUser));
        StepVerifier.create(userService.getUserById(testUser.getId()))
                .expectNext(testUser)
                .verifyComplete();
    }

    @Test
    void addUpdateUser_shouldUpdateUser() {
        Mockito.when(userRepo.getUserByID(testUser.getId())).thenReturn(Mono.just(testUser));
        Mockito.when(userRepo.updateUser(testUser)).thenReturn(Mono.just(testUser));

        StepVerifier.create(userService.AddUpdateUser(testUser))
                .expectNext(testUser)
                .verifyComplete();

        Mockito.when(userRepo.getUserByID(testUser.getId())).thenReturn(Mono.empty());
        Mockito.when(userRepo.addUser(testUser)).thenReturn(Mono.just(testUser));
        StepVerifier.create(userService.AddUpdateUser(testUser))
                .expectNext(testUser)
                .verifyComplete();
    }

    @Test
    void updateUserFromOAuthUserInfoGoogle() {
        // Given
        OAuth2UserInfoGoogle oAuth2UserInfoGoogle = new OAuth2UserInfoGoogle("updated", "updated", "updated", "testID");

        // When
        UserEntity updatedUser = userService.updateUserFromOAuthUserInfoGoogle(oAuth2UserInfoGoogle, testUser);

        // Then - Assertions for different fields
        Assertions.assertThat(updatedUser.getId()).isEqualTo(testUser.getId());
        Assertions.assertThat(updatedUser.getName()).isEqualTo("updated");
        Assertions.assertThat(updatedUser.getEmail()).isEqualTo("updated");
        Assertions.assertThat(updatedUser.getPicture()).isEqualTo("updated");
    }
}
