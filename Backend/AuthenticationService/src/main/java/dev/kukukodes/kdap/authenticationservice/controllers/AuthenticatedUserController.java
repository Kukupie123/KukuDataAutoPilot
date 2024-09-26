package dev.kukukodes.kdap.authenticationservice.controllers;

import dev.kukukodes.kdap.authenticationservice.constants.AccessLevelConst;
import dev.kukukodes.kdap.authenticationservice.entity.user.KDAPUserEntity;
import dev.kukukodes.kdap.authenticationservice.helpers.SecurityHelper;
import dev.kukukodes.kdap.authenticationservice.models.ResponseModel;
import dev.kukukodes.kdap.authenticationservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/authenticated")
public class AuthenticatedUserController {

    private final UserService userService;
    private final SecurityHelper securityHelper;

    public AuthenticatedUserController(@Qualifier(AccessLevelConst.SELF) UserService userService, SecurityHelper securityHelper) {
        this.userService = userService;
        this.securityHelper = securityHelper;
    }

    private KDAPUserEntity removeSensitiveData(KDAPUserEntity kdapUserEntity) {
        kdapUserEntity.setPassword(null);
        kdapUserEntity.setUpdated(null);
        return kdapUserEntity;
    }

    /**
     * Get all users or a specific user by their ID.
     *
     * @param id    path param for userID or "*" to get all users. if empty then returns self data
     * @param skip  number of records to skip.
     * @param limit maximum number of records to retrieve.
     * @return {@link KDAPUserEntity} or a list of users.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<ResponseModel<List<KDAPUserEntity>>>> getUser(@PathVariable(required = false) String id, @RequestParam int skip, @RequestParam int limit) {
        if (id == null) {
            log.info("Getting self info");
            return securityHelper.getKDAPAuthenticated()
                    .flatMap(authenticated -> userService.getUserById(authenticated.getUser().getId()))
                    .map(user -> ResponseModel.success("user data", List.of(user)))
                    .switchIfEmpty(Mono.just(ResponseModel.buildResponse("Failed to get user info", List.of(), 500)))
                    .onErrorResume(throwable -> Mono.just(ResponseModel.buildResponse(throwable.getMessage(), null, 500)))
                    ;
        }
        if (id.equals("*")) {
            log.info("Getting all users");
            return userService.getAllUsers(skip, limit)
                    .collectList()
                    .map(kdapUserEntities -> ResponseModel.success("", kdapUserEntities))
                    .onErrorResume(throwable -> Mono.just(ResponseModel.buildResponse(throwable.getMessage(), null, 500)));
        }
        log.info("Getting user with id {}", id);
        return userService.getUserById(id)
                .map(user -> ResponseModel.success("", List.of(user)))
                .onErrorResume(throwable -> Mono.just(ResponseModel.buildResponse(throwable.getMessage(), null, 500)));
    }

    /**
     * Get the information of the authenticated user (self).
     *
     * @return {@link KDAPUserEntity}
     */
    @GetMapping("/self")
    public Mono<ResponseEntity<ResponseModel<KDAPUserEntity>>> getSelfUser() {
        return securityHelper.getKDAPAuthenticated()
                .doOnNext(authenticated -> log.info("Getting self : {}", authenticated.getUser().getId()))
                .flatMap(authenticated -> userService.getUserById(authenticated.getUser().getId()))
                .map(this::removeSensitiveData)
                .map(user -> ResponseModel.success("", user))
                .onErrorResume(throwable -> Mono.just(ResponseModel.buildResponse(throwable.getMessage(), null, 500)));
    }


    /**
     * Update user info by ID. Updating self is allowed if not admin.
     *
     * @param payload updated user data.
     * @return {@link KDAPUserEntity}
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<ResponseModel<KDAPUserEntity>>> updateUser(@RequestBody KDAPUserEntity payload) {
        log.info("updating user {}", payload.getId());
        return userService.updateUser(payload)
                .map(user -> ResponseModel.success("", user))
                .onErrorResume(throwable -> Mono.just(ResponseModel.buildResponse(throwable.getMessage(), null, 500)));
    }

    /**
     * Delete self user (authenticated user).
     *
     * @return {@link Boolean} status of deletion.
     */
    @DeleteMapping("/self")
    public Mono<ResponseEntity<ResponseModel<Boolean>>> deleteSelfUser() {
        return securityHelper.getKDAPAuthenticated()
                .doOnNext(authenticated -> log.info("Deleting self {}", authenticated.getUser()))
                .flatMap(authenticated -> userService.deleteUser(authenticated.getUser().getId()))
                .map(deleted -> ResponseModel.success("", deleted))
                .onErrorResume(throwable -> Mono.just(ResponseModel.buildResponse(throwable.getMessage(), null, 500)));
    }

    /**
     * Delete user by ID.
     *
     * @param id userID of the user to delete.
     * @return {@link Boolean} status of deletion.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<ResponseModel<Boolean>>> deleteUser(@PathVariable String id) {
        log.info("Deleting user : {}", id);
        return userService.deleteUser(id)
                .map(deleted -> {
                    if (deleted) return ResponseModel.success("Deleted", true);
                    return ResponseModel.buildResponse("Failed to delete", false, 500);
                })
                .onErrorResume(throwable -> Mono.just(ResponseModel.buildResponse(throwable.getMessage(), null, 500)));
    }
}
