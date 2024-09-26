package dev.kukukodes.kdap.authenticationservice.controllers;

import dev.kukukodes.kdap.authenticationservice.constants.AccessLevelConst;
import dev.kukukodes.kdap.authenticationservice.entity.user.KDAPUserEntity;
import dev.kukukodes.kdap.authenticationservice.helpers.SecurityHelper;
import dev.kukukodes.kdap.authenticationservice.models.authentication.KDAPAuthenticated;
import dev.kukukodes.kdap.authenticationservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class AuthenticatedUserControllerTest {

    private WebTestClient webTestClient;

    @Mock(name = AccessLevelConst.SELF)
    private UserService userService;

    @Mock
    private SecurityHelper securityHelper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        AuthenticatedUserController authenticatedUserController = new AuthenticatedUserController(userService, securityHelper);
        webTestClient = WebTestClient.bindToController(authenticatedUserController).build();
    }

    @Nested
    class GetUser {
        @Test
        void shouldGetAllUsers() {
            List<KDAPUserEntity> users = Arrays.asList(
                    new KDAPUserEntity("1", "User 1", "password1", LocalDate.now(), LocalDate.now(), "user1@test.com", "picture1.jpg"),
                    new KDAPUserEntity("2", "User 2", "password2", LocalDate.now(), LocalDate.now(), "user2@test.com", "picture2.jpg")
            );
            when(userService.getAllUsers(anyInt(), anyInt())).thenReturn(Flux.fromIterable(users));

            webTestClient.get().uri("/api/authenticated/*?skip=0&limit=10")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.data.length()").isEqualTo(2)
                    .jsonPath("$.data[0].id").isEqualTo("1")
                    .jsonPath("$.data[1].id").isEqualTo("2");
        }

        @Test
        void shouldGetUserById() {
            KDAPUserEntity user = new KDAPUserEntity("1", "User 1", "password1", LocalDate.now(), LocalDate.now(), "user1@test.com", "picture1.jpg");
            when(userService.getUserById("1")).thenReturn(Mono.just(user));

            webTestClient.get().uri("/api/authenticated/1?skip=0&limit=10")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.data[0].id").isEqualTo("1")
                    .jsonPath("$.data[0].name").isEqualTo("User 1");
        }
    }

    @Nested
    class GetSelfUser {
        @Test
        void shouldGetSelfUser() {
            KDAPUserEntity user = new KDAPUserEntity("1", "User 1", "password1", LocalDate.now(), LocalDate.now(), "user1@test.com", "picture1.jpg");
            when(securityHelper.getKDAPAuthenticated()).thenReturn(Mono.just(new KDAPAuthenticated("SELF", "TEST", user)));
            when(userService.getUserById("1")).thenReturn(Mono.just(user));

            webTestClient.get().uri("/api/authenticated/self")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.data.id").isEqualTo("1")
                    .jsonPath("$.data.name").isEqualTo("User 1")
                    .jsonPath("$.data.password").doesNotExist();
        }
    }

    @Nested
    class UpdateUser {
        @Test
        void shouldUpdateSelfUser() {
            KDAPUserEntity updatedUser = new KDAPUserEntity("1", "Updated User 1", "newpassword1", LocalDate.now(), LocalDate.now(), "updateduser1@test.com", "updatedpicture1.jpg");
            when(userService.updateUser(any(KDAPUserEntity.class))).thenReturn(Mono.just(updatedUser));

            webTestClient.put().uri("/api/authenticated/self")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(updatedUser)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.data.id").isEqualTo("1")
                    .jsonPath("$.data.name").isEqualTo("Updated User 1");
        }

        @Test
        void shouldUpdateUserById() {
            KDAPUserEntity updatedUser = new KDAPUserEntity("1", "Updated User 1", "newpassword1", LocalDate.now(), LocalDate.now(), "updateduser1@test.com", "updatedpicture1.jpg");
            when(userService.updateUser(any(KDAPUserEntity.class))).thenReturn(Mono.just(updatedUser));

            webTestClient.put().uri("/api/authenticated/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(updatedUser)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.data.id").isEqualTo("1")
                    .jsonPath("$.data.name").isEqualTo("Updated User 1");
        }
    }

    @Nested
    class DeleteUser {
        @Test
        void shouldDeleteSelfUser() {
            KDAPUserEntity user = new KDAPUserEntity("1", "User 1", "password1", LocalDate.now(), LocalDate.now(), "user1@test.com", "picture1.jpg");
            when(securityHelper.getKDAPAuthenticated()).thenReturn(Mono.just(new KDAPAuthenticated("SELF", "TEST", user)));
            when(userService.deleteUser("1")).thenReturn(Mono.just(true));

            webTestClient.delete().uri("/api/authenticated/self")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.data").isEqualTo(true);
        }

        @Test
        void shouldDeleteUserById() {
            when(userService.deleteUser("1")).thenReturn(Mono.just(true));

            webTestClient.delete().uri("/api/authenticated/1")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.data").isEqualTo(true);
        }
    }
}