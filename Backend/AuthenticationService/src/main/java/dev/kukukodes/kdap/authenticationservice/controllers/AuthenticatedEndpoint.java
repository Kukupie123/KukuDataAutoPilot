package dev.kukukodes.kdap.authenticationservice.controllers;

import dev.kukukodes.kdap.authenticationservice.dto.user.UserRequestDTO;
import dev.kukukodes.kdap.authenticationservice.entity.UserEntity;
import dev.kukukodes.kdap.authenticationservice.models.KDAPUserAuthentication;
import dev.kukukodes.kdap.authenticationservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.InvalidPropertiesFormatException;

/**
 * Endpoints:
 * <p>
 * GET '/'
 *      - Returns {@link UserRequestDTO} info based on the JWT token in the Bearer Authorization header.
 * <p>
 * GET '/{id}'
 *      - Returns {@link UserRequestDTO} for the specified user.
 *      - Access is denied if attempting to retrieve info of another user without Admin role.
 * <p>
 * PUT '/{id}'
 *      - Update the specified user.
 *      - Access is denied if attempting to update another user without Admin role.
 * <p>
 * PUT '/'
 *      - Update user info based on the user ID extracted from the JWT token in the Bearer Authorization header.
 * <p>
 * DELETE '/'
 *      - Delete the user account based on the user ID extracted from the JWT token in the Bearer Authorization header.
 * <p>
 * DELETE '/{id}'
 *      - Delete the specified user.
 *      - Access is denied unless the requester has Admin role.
 */

@Slf4j
@RestController
@RequestMapping("/api/authenticated")
public class AuthenticatedEndpoint {

    private final UserService userService;

    public AuthenticatedEndpoint(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/")
    public Mono<ResponseEntity<UserRequestDTO>> getUserFromToken(@RequestHeader("Authorization") String authToken, @Value("${superemail}") String superEmail) {
        if (authToken == null || !authToken.startsWith("Bearer ")) {
            return Mono.error(new InvalidPropertiesFormatException("Bearer token is invalid"));
        }
        log.info("Getting user from token");
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .cast(KDAPUserAuthentication.class)
                .flatMap(kdapUserAuthentication -> userService.getUserById(kdapUserAuthentication.getId()))
                .map(user -> ResponseEntity.ok(UserRequestDTO.fromUserEntity(user, true, superEmail)));
    }

    /**
     * Get user data. Access will be denied if attempting to get user Data of someone else without having admin role
     * <p>
     * Eg :- '/userID'
     * <p>
     *
     * @return  {@link UserRequestDTO}
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<UserRequestDTO>> getUser(@PathVariable String id, @Value("${superemail}") String superEmail) {
        if (id == null) {
            return Mono.error(new IllegalArgumentException("id is null"));
        }
        log.info("Getting user from id parameter : {}", id);
        return userService.getUserById(id)
                //Do not send password
                .map(user -> UserRequestDTO.fromUserEntity(user, false, superEmail))
                .flatMap(userRequestDTO -> Mono.just(ResponseEntity.ok(userRequestDTO)))
                ;
    }

    /**
     * Update user. Access will be denied if attempting to update another user without admin role.
     *
     * @param payload {@link UserRequestDTO}
     * @return updated user {@link  UserRequestDTO}
     */
    @PutMapping("/")
    public Mono<ResponseEntity<UserRequestDTO>> updateUser(@RequestBody UserRequestDTO payload, ServerWebExchange exchange, @Value("${superemail}") String superEmail) {
        log.info("Updating user : {}", payload);
        return userService.getUserById(payload.getId())
                //Get user from database to create new userEntity object with fields that are not present in payload
                .flatMap(dbUser -> userService.updateUser(new UserEntity(dbUser.getId(), payload.getName(), payload.getPassword(), dbUser.getCreated(), dbUser.getUpdated(), payload.getEmail(), dbUser.getPicture())))
                .map(user -> UserRequestDTO.fromUserEntity(user, true, superEmail))
                .map(ResponseEntity::ok)
                ;
    }

    /**
     * Delete user. Access will be denied if attempting to delete another user without admin role
     *
     * @param id id of the user to delete
     * @return 200 status if deleted or else internal server error
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<String>> deleteUser(@PathVariable String id) {
        log.info("Deleting user : {}", id);
        return userService.deleteUser(id)
                .map(deleted -> deleted ? ResponseEntity.ok().build() : ResponseEntity.internalServerError().build())
                ;
    }
}
