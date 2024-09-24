package dev.kukukodes.kdap.authenticationservice.repo;

import dev.kukukodes.kdap.authenticationservice.entity.UserEntity;
import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@DataR2dbcTest
class UserRepoTest {

    @Autowired
    private ConnectionFactory connectionFactory;

    private UserRepo userRepo;

    @BeforeEach
    void setUp() {
        userRepo = new UserRepo(connectionFactory);
    }
    @AfterEach
    void tearDown() {
        userRepo.deleteUserByID("1").block();
    }

    @Test
    void addUser() {
        UserEntity user = new UserEntity();
        user.setId("1");
        user.setName("testUser");
        user.setEmail("test@example.com");

        StepVerifier.create(userRepo.addUser(user))
                .assertNext(savedUser -> {
                    assertNotNull(savedUser);
                    assertEquals("1", savedUser.getId());
                    assertEquals("testUser", savedUser.getName());
                    assertEquals("test@example.com", savedUser.getEmail());
                })
                .verifyComplete();
    }

    @Test
    void updateUser() {
        UserEntity user = new UserEntity();
        user.setId("1");
        user.setName("testUser");
        user.setEmail("test@example.com");

        StepVerifier.create(userRepo.addUser(user)
                        .flatMap(savedUser -> {
                            savedUser.setName("updatedUser");
                            return userRepo.updateUser(savedUser);
                        }))
                .assertNext(updatedUser -> {
                    assertNotNull(updatedUser);
                    assertEquals("1", updatedUser.getId());
                    assertEquals("updatedUser", updatedUser.getName());
                    assertEquals("test@example.com", updatedUser.getEmail());
                })
                .verifyComplete();
    }

    @Test
    void getUserByID() {
        UserEntity user = new UserEntity();
        user.setId("1");
        user.setName("testUser");
        user.setEmail("test@example.com");

        StepVerifier.create(userRepo.addUser(user)
                        .flatMap(savedUser -> userRepo.getUserByID("1")))
                .assertNext(fetchedUser -> {
                    assertNotNull(fetchedUser);
                    assertEquals("1", fetchedUser.getId());
                    assertEquals("testUser", fetchedUser.getName());
                    assertEquals("test@example.com", fetchedUser.getEmail());
                })
                .verifyComplete();
    }

    @Test
    void deleteUserByID() {
        UserEntity user = new UserEntity();
        user.setId("1");
        user.setName("testUser");
        user.setEmail("test@example.com");

        StepVerifier.create(userRepo.addUser(user)
                        .flatMap(savedUser -> userRepo.deleteUserByID("1")))
                .expectNext(true)
                .verifyComplete();

        StepVerifier.create(userRepo.getUserByID("1"))
                .verifyComplete();
    }
}
