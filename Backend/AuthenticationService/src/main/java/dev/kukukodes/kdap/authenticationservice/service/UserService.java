package dev.kukukodes.kdap.authenticationservice.service;

import dev.kukukodes.kdap.authenticationservice.constants.AccessLevelConst;
import dev.kukukodes.kdap.authenticationservice.entity.user.KDAPUserEntity;
import dev.kukukodes.kdap.authenticationservice.helpers.SecurityHelper;
import dev.kukukodes.kdap.authenticationservice.models.authentication.KDAPAuthenticated;
import dev.kukukodes.kdap.authenticationservice.publishers.UserEventPublisher;
import dev.kukukodes.kdap.authenticationservice.repo.IUserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Slf4j
public class UserService {
    private final IUserRepo userRepo;
    private final CacheService cacheService;
    private final SecurityHelper securityHelper;
    private final UserEventPublisher userEventPublisher;
    private final boolean fullAccess; //If true, will ignore current user's role

    public UserService(IUserRepo userRepo, CacheService cacheService, SecurityHelper securityHelper, UserEventPublisher userEventPublisher, boolean fullAccess) {
        this.userRepo = userRepo;
        this.cacheService = cacheService;
        this.securityHelper = securityHelper;
        this.userEventPublisher = userEventPublisher;
        this.fullAccess = fullAccess;
    }

    /**
     * Adds a new user to database. ONLY ADMIN can do it
     */
    public Mono<KDAPUserEntity> addUser(KDAPUserEntity userToAdd) {
        if (fullAccess) {
            return userRepo.addUser(userToAdd);
        }
        return securityHelper.getKDAPAuthenticated()
                .flatMap(authenticated -> {
                    if (!hasAdminRole(authenticated)) {
                        return Mono.error(new AccessDeniedException("Not Logged in as admin : " + authenticated.getUser().getId()));
                    }
                    return userRepo.addUser(userToAdd).doOnSuccess(user -> userEventPublisher.publishUserAddedEvent(userToAdd));
                });
    }

    /**
     * Update a user. If not admin or full access, updating self is possible but not updating others
     *
     * @param user the updated user data
     * @return updated user
     */
    public Mono<KDAPUserEntity> updateUser(KDAPUserEntity user) {
        // Step 1: Determine if we need to check access
        Mono<String> userIdMono;
        if (fullAccess) {
            userIdMono = Mono.just(user.getId());
        } else {
            userIdMono = securityHelper.getKDAPAuthenticated()
                    .flatMap(authenticated -> {
                        //Need to be admin or only allowed to update self
                        if (!hasAdminRole(authenticated) && !authenticated.getUser().getId().equals(user.getId())) {
                            return Mono.error(new AccessDeniedException("Not Logged in as admin : " + authenticated.getUser().getId()));
                        }
                        return Mono.just(user.getId());
                    });
        }
        // Step 2: Retrieve the user from cache or database
        return userIdMono.flatMap(userId -> {
                    KDAPUserEntity cachedUser = cacheService.getUser(userId);
                    if (cachedUser != null) {
                        return Mono.just(cachedUser);
                    } else {
                        return userRepo.getUserByID(userId)
                                .switchIfEmpty(Mono.error(new RuntimeException("User not found: " + userId)));
                    }
                })
                // Step 3: Update the user if there are changes
                .flatMap(dbUser -> {
                    log.info("Updating from {} to {}", dbUser, user);
                    boolean hasChanges = false;

                    if (user.getEmail() != null && !user.getEmail().equals(dbUser.getEmail())) {
                        dbUser.setEmail(user.getEmail());
                        hasChanges = true;
                    }
                    if (user.getPassword() != null && !user.getPassword().equals(dbUser.getPassword())) {
                        dbUser.setPassword(user.getPassword());
                        hasChanges = true;
                    }
                    if (user.getPicture() != null && !user.getPicture().equals(dbUser.getPicture())) {
                        dbUser.setPicture(user.getPicture());
                        hasChanges = true;
                    }
                    if (user.getName() != null && !user.getName().equals(dbUser.getName())) {
                        dbUser.setName(user.getName());
                        hasChanges = true;
                    }

                    if (!hasChanges) {
                        log.warn("No changes detected for user {}", dbUser.getId());
                        return Mono.empty(); //DoOnNext will not be called
                    }
                    dbUser.setUpdated(LocalDate.now());
                    log.info("Updating user info from {} to {}", dbUser, user);
                    return userRepo.updateUser(dbUser);
                })
                // Step 4: Handle successful update.
                .doOnNext(updatedUser -> {
                    cacheService.removeUser(updatedUser.getId());
                    userEventPublisher.publishUserUpdatedEvent(updatedUser);
                });
    }

    /**
     * Get user. Unless admin or full access, getting user who isn't self is not allowed
     *
     * @param id id of the user to get
     * @return user
     */
    public Mono<KDAPUserEntity> getUserById(String id) {
        Mono<String> idMono;
        if (fullAccess) {
            idMono = Mono.just(id);
        } else {
            idMono = securityHelper.getKDAPAuthenticated()
                    .flatMap(authenticated -> {
                        if (!hasAdminRole(authenticated) && !authenticated.getUser().getId().equals(id)) {
                            return Mono.error(new AccessDeniedException("Not Logged in as admin : " + authenticated.getUser().getId()));
                        }
                        return Mono.just(id);
                    });
        }

        return idMono
                .flatMap(s -> {
                    var user = cacheService.getUser(s);
                    if (user == null) {
                        log.info("failed to get user from database");
                        return Mono.empty();
                    }
                    return Mono.just(user);
                })
                .switchIfEmpty(userRepo.getUserByID(id))
                ;
    }

    /**
     * Delete a user. Unless admin or full access, only deleting self is allowed
     */
    public Mono<Boolean> deleteUser(String userID) {
        Mono<String> idMono;
        if (fullAccess) {
            idMono = Mono.just(userID);
        } else {
            idMono = securityHelper.getKDAPAuthenticated()
                    .flatMap(authenticated -> {
                        if (!hasAdminRole(authenticated) && !authenticated.getUser().getId().equals(userID)) {
                            return Mono.error(new AccessDeniedException("Not Logged in as admin : " + authenticated.getUser().getId()));
                        }
                        return Mono.just(userID);
                    });
        }
        cacheService.removeUser(userID);
        return idMono
                .flatMap(userRepo::deleteUserByID)
                .doOnSuccess(aBoolean -> userEventPublisher.publishUserDeletedEvent(userID));
    }

    public Flux<KDAPUserEntity> getAllUsers(int skip, int limit) {
        if (!fullAccess) {
            return securityHelper.getKDAPAuthenticated()
                    .flux()
                    .flatMap(authenticated -> {
                        if (!hasAdminRole(authenticated)) {
                            return Mono.error(new AccessDeniedException("Not Logged in as admin : " + authenticated.getUser().getId()));
                        }
                        return userRepo.getAllUsers(skip, limit);
                    });
        }
        return userRepo.getAllUsers(skip, limit);
    }

    private Boolean hasAdminRole(KDAPAuthenticated kdapUserAuthentication) {
        return kdapUserAuthentication.getAccessLevel().equals(AccessLevelConst.ADMIN);
    }

}
