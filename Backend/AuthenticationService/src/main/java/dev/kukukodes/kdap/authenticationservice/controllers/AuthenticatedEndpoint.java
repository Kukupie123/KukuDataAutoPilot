package dev.kukukodes.kdap.authenticationservice.controllers;

import dev.kukukodes.kdap.authenticationservice.entity.user.KDAPUserEntity;
import dev.kukukodes.kdap.authenticationservice.models.ResponseModel;
import dev.kukukodes.kdap.authenticationservice.models.userModels.KDAPUserAuthentication;
import dev.kukukodes.kdap.authenticationservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.InvalidPropertiesFormatException;

/**
 * Endpoints:
 * <p>
 * GET '/'
 * - Returns {@link KDAPUserEntity} info based on the JWT token in the Bearer Authorization header.
 * <p>
 * GET '/{id}'
 * - Returns {@link KDAPUserEntity} for the specified user.
 * - Access is denied if attempting to retrieve info of another user without Admin role.
 * <p>
 * PUT '/'
 * - Update user info based on the user ID extracted from the JWT token in the Bearer Authorization header.
 * <p>
 * DELETE '/'
 * - Delete the user account based on the user ID extracted from the JWT token in the Bearer Authorization header.
 * <p>
 * DELETE '/{id}'
 * - Delete the specified user.
 * - Access is denied unless the requester has Admin role.
 */

@Slf4j
@RestController
@RequestMapping("/api/authenticated")
public class AuthenticatedEndpoint {

    private final UserService userService;

    public AuthenticatedEndpoint(UserService userService) {
        this.userService = userService;
    }

    private KDAPUserEntity removeSensitiveData(KDAPUserEntity kdapUserEntity) {
        kdapUserEntity.setPassword(null);
        kdapUserEntity.setUpdated(null);
        return kdapUserEntity;
    }

    /**
     * Get user info by extracting userID from authorization bearer token
     *
     * @return {@link KDAPUserEntity}
     */
    @GetMapping("/")
    public Mono<ResponseEntity<ResponseModel<KDAPUserEntity>>> getUserFromToken(@RequestHeader("Authorization") String authToken, @Value("${superemail}") String superEmail) {
        if (authToken == null || !authToken.startsWith("Bearer ")) {
            return Mono.error(new InvalidPropertiesFormatException("Bearer token is invalid"));
        }
        log.info("Getting user from token");
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .cast(KDAPUserAuthentication.class)
                .flatMap(kdapUserAuthentication -> userService.getUserById(kdapUserAuthentication.getId()))
                .map(this::removeSensitiveData)
                .map(user -> ResponseModel.success("Received", user))
                .onErrorResume(throwable -> Mono.just(ResponseModel.buildResponse(throwable.getMessage(), null, 500)))
                ;
    }

    /**
     * Get user info of the userID passed as path param
     *
     * @param id path param
     * @return {@link  KDAPUserEntity}
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<ResponseModel<KDAPUserEntity>>> getUser(@PathVariable String id, @Value("${superemail}") String superEmail) {
        if (id == null) {
            return Mono.error(new IllegalArgumentException("id is null"));
        }
        log.info("Getting user from id parameter : {}", id);
        return userService.getUserById(id)
                //Do not send password
                .map(this::removeSensitiveData)
                .flatMap(user -> Mono.just(ResponseModel.success("Received", user)))
                .onErrorResume(throwable -> Mono.just(ResponseModel.buildResponse(throwable.getMessage(), null, 500)))
                ;
    }

    /**
     * Update user. If attempting to update another user, it will be defined if admin role not found
     *
     * @param payload updated user data along with id
     * @return {@link  KDAPUserEntity}
     */
    @PutMapping("/")
    public Mono<ResponseEntity<ResponseModel<KDAPUserEntity>>> updateUser(@RequestBody KDAPUserEntity payload, @Value("${superemail}") String superEmail) {
        log.info("Updating user : {}", payload);
        return userService.getUserById(payload.getId())
                //Get user from database to create new userEntity object with fields that are not present in payload
                .flatMap(dbUser -> userService.updateUser(new KDAPUserEntity(dbUser.getId(), payload.getName(), payload.getPassword(), dbUser.getCreated(), dbUser.getUpdated(), payload.getEmail(), dbUser.getPicture())))
                .map(this::removeSensitiveData)
                .map(user -> ResponseModel.success("Updated", user))
                .onErrorResume(throwable -> Mono.just(ResponseModel.buildResponse(throwable.getMessage(), null, 500)))
                ;
    }

    /**
     * Delete user that is currently logged in
     *
     * @return {@link KDAPUserEntity}
     */
    @DeleteMapping("/")
    public Mono<ResponseEntity<ResponseModel<Boolean>>> deleteSelfUser() {
        log.info("Deleting self user");
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .cast(KDAPUserAuthentication.class)
                .flatMap(kdapUserAuthentication -> userService.deleteUser(kdapUserAuthentication.getId()))
                .map(deleted -> {
                    if (deleted)
                        return ResponseModel.success("Deleted", true);
                    return ResponseModel.buildResponse("failed to delete", false, 500);
                })
                .onErrorResume(throwable -> Mono.just(ResponseModel.buildResponse(throwable.getMessage(), null, 500)));
    }

    /**
     * Delete user. Access will be denied if attempting to delete another user without admin role
     *
     * @param id id of the user to delete
     * @return 200 status if deleted or else internal server error
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<ResponseModel<Boolean>>> deleteUser(@PathVariable String id) {
        log.info("Deleting user : {}", id);
        return userService.deleteUser(id)
                .map(deleted -> {
                    if (deleted)
                        return ResponseModel.success("Deleted", true);
                    return ResponseModel.buildResponse("failed to delete", false, 500);
                })
                .onErrorResume(throwable -> Mono.just(ResponseModel.buildResponse(throwable.getMessage(), null, 500)));
    }
}
