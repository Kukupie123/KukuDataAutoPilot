package dev.kukukodes.kdap.authenticationservice.controllers;

import dev.kukukodes.kdap.authenticationservice.entity.user.KDAPUserEntity;
import dev.kukukodes.kdap.authenticationservice.enums.UserRole;
import dev.kukukodes.kdap.authenticationservice.helpers.SecurityHelper;
import dev.kukukodes.kdap.authenticationservice.models.ResponseModel;
import dev.kukukodes.kdap.authenticationservice.models.userModels.KDAPUserAuthentication;
import dev.kukukodes.kdap.authenticationservice.models.userModels.KDAPUserPayload;
import dev.kukukodes.kdap.authenticationservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.security.auth.login.AccountNotFoundException;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.List;

/**
 * Endpoints:
 * <p>
 * GET '/'
 * - Returns {@link KDAPUserEntity} info based on the JWT token in the Bearer Authorization header.
 * <p>
 * GET '/{id}'
 * - Returns {@link KDAPUserEntity} for the specified user. If id is '*' then all users will be returned.
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
    private final SecurityHelper securityHelper;

    public AuthenticatedEndpoint(UserService userService, SecurityHelper securityHelper) {
        this.userService = userService;
        this.securityHelper = securityHelper;
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
    public Mono<ResponseEntity<ResponseModel<KDAPUserPayload>>> getUserFromToken(@RequestHeader("Authorization") String authToken, @Value("${superemail}") String superEmail) {
        if (authToken == null || !authToken.startsWith("Bearer ")) {
            return Mono.error(new InvalidPropertiesFormatException("Bearer token is invalid"));
        }
        log.info("Getting user from token");
        return securityHelper.getKDAPUserAuthentication()
                .switchIfEmpty(Mono.error(new AccountNotFoundException("No KDAP User authentication found")))
                .zipWhen(kdapUserAuthentication -> userService.getUserById(kdapUserAuthentication.getId()))
                .map(authenticationKDAPUserEntityTuple2 -> KDAPUserPayload.fromKDAPUserEntity(authenticationKDAPUserEntityTuple2.getT2(), authenticationKDAPUserEntityTuple2.getT1().getUserRole()))
                .map(user -> ResponseModel.success("Received", user))
                .onErrorResume(throwable -> Mono.just(ResponseModel.buildResponse(throwable.getMessage(), null, 500)))
                ;
    }

    /**
     * Get user info of the userID passed as path param or add users if id is *
     *
     * @param id path param
     * @return {@link  KDAPUserEntity}
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<ResponseModel<List<KDAPUserPayload>>>> getUser(@PathVariable String id,
                                                                              @Value("${superemail}") String superEmail) {
        if (id == null || id.trim().isEmpty()) {
            return Mono.just(ResponseModel.buildResponse("No id provided", null, 401));
        }

        if (id.equals("*")) {
            log.info("Getting all users as an admin");
            Mono<ResponseEntity<ResponseModel<List<KDAPUserPayload>>>> usersList = userService.getAllUsers()
                    .collectList()
                    .switchIfEmpty(Mono.error(new AccountNotFoundException("Not found")))
                    .zipWith(securityHelper.getKDAPUserAuthentication())
                    .map(objects -> {
                        List<KDAPUserPayload> userPayloads = new ArrayList<>();
                        for (var user : objects.getT1()) {
                            if (user.getId().equals(objects.getT2().getId())) {
                                userPayloads.add(KDAPUserPayload.fromKDAPUserEntity(user, objects.getT2().getUserRole()));
                                continue;
                            }
                            if (user.getEmail().equals(superEmail)) {
                                userPayloads.add(KDAPUserPayload.fromKDAPUserEntity(user, UserRole.ADMIN));
                                continue;
                            }
                            userPayloads.add(KDAPUserPayload.fromKDAPUserEntity(user, UserRole.USER));
                        }
                        return userPayloads;
                    })
                    .map(kdapUserPayloads -> ResponseModel.success("Received", kdapUserPayloads))
                    .onErrorResume(throwable -> Mono.just(ResponseModel.buildResponse(throwable.getMessage(), null, 500)));
            return usersList;
        } else {
            log.info("Getting user from id parameter : {}", id);
            return userService.getUserById(id)
                    .switchIfEmpty(Mono.error(new Exception("User not found")))
                    .zipWith(securityHelper.getKDAPUserAuthentication())
                    .map(tuple -> {
                        if (tuple.getT1().getId().equals(tuple.getT2().getId())) {
                            return KDAPUserPayload.fromKDAPUserEntity(tuple.getT1(), tuple.getT2().getUserRole());
                        }
                        if (tuple.getT1().getEmail().equals(superEmail)) {
                            return KDAPUserPayload.fromKDAPUserEntity(tuple.getT1(), UserRole.ADMIN);
                        }
                        return KDAPUserPayload.fromKDAPUserEntity(tuple.getT1(), UserRole.USER);
                    })
                    .map(user -> ResponseModel.success("User found", List.of(user)))
                    .onErrorResume(throwable -> {
                        log.error("Error fetching user: ", throwable);
                        return Mono.just(ResponseModel.buildResponse(throwable.getMessage(), null, 500));
                    });
        }

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
