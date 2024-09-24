package dev.kukukodes.kdap.authenticationservice.service;

import dev.kukukodes.kdap.authenticationservice.entity.user.KDAPUserEntity;
import dev.kukukodes.kdap.authenticationservice.enums.UserRole;
import dev.kukukodes.kdap.authenticationservice.helpers.SecurityHelper;
import dev.kukukodes.kdap.authenticationservice.models.userModels.KDAPUserAuthentication;
import dev.kukukodes.kdap.authenticationservice.models.userModels.OAuth2UserInfoGoogle;
import dev.kukukodes.kdap.authenticationservice.publishers.UserEventPublisher;
import dev.kukukodes.kdap.authenticationservice.repo.IUserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.InputMismatchException;

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
        if (!fullAccess)
            return isKDAPUserAuthenticated(securityHelper.getKDAPUserAuthentication())
                    .flatMap(kdapUserAuthentication -> {
                        if (kdapUserAuthentication.getUserRole() != UserRole.ADMIN) {
                            log.info("Access denied {}", kdapUserAuthentication.getUserRole().toString());
                            return Mono.error(new AccessDeniedException("Access denied for role " + kdapUserAuthentication.getUserRole().toString()));
                        }
                        return userRepo.addUser(userToAdd).doOnSuccess(userEventPublisher::publishUserAddedEvent);
                    })
                    ;
        log.info("Adding user as a super user {}", userToAdd);
        return userRepo.addUser(userToAdd).doOnSuccess(userEventPublisher::publishUserAddedEvent);
    }

    /**
     * Update existing user
     */
    public Mono<KDAPUserEntity> updateUser(KDAPUserEntity user) {
        Mono<String> idMono;
        if (!fullAccess) {
            log.info("No super access. Checking security user");
            idMono = isKDAPUserAuthenticated(securityHelper.getKDAPUserAuthentication())
                    // Needs to be admin or else only allowed to update self info
                    .flatMap(kdapUserAuthentication -> {
                        if (kdapUserAuthentication.getUserRole() != UserRole.ADMIN) {
                            if (!user.getId().equals(kdapUserAuthentication.getId())) {
                                log.info("Access denied {}", kdapUserAuthentication.getUserRole().toString());
                                return Mono.error(new AccessDeniedException("Access denied for role " + kdapUserAuthentication.getUserRole().toString()));
                            }
                        }
                        return Mono.just(kdapUserAuthentication.getId());
                    });
        } else {
            idMono = Mono.just(user.getId());
        }

        return idMono
                //get user from database
                .flatMap(userRepo::getUserByID)
                //compare the values to determine if they are different and update if not same
                .flatMap(dbUser -> {
                    if (
                            user.getId().equals(dbUser.getId()) &&
                                    (user.getEmail() == null || user.getEmail().equals(dbUser.getEmail())) &&
                                    (user.getPassword() == null || user.getPassword().equals(dbUser.getPassword())) &&
                                    (user.getPicture() == null || user.getPicture().equals(dbUser.getPicture())) &&
                                    (user.getName() == null || user.getName().equals(dbUser.getName()))
                    ) {
                        log.warn("Nothing to update");
                        return Mono.just(dbUser);
                    }
                    user.setUpdated(LocalDate.now());
                    user.setCreated(dbUser.getCreated());
                    log.info("Updating user info from {} to {}", dbUser, user);
                    dbUser.setUpdated(LocalDate.now());
                    //Preventing updating created time by others
                    dbUser.setCreated(dbUser.getCreated());

                    // Only update fields that are non-null in the incoming user object
                    if (user.getPassword() != null) {
                        dbUser.setPassword(user.getPassword());
                    }
                    if (user.getPicture() != null) {
                        dbUser.setPicture(user.getPicture());
                    }
                    if (user.getEmail() != null) {
                        dbUser.setEmail(user.getEmail());
                    }
                    if (user.getName() != null) {
                        dbUser.setName(user.getName());
                    }

                    return userRepo.updateUser(dbUser)
                            .doOnSuccess(updatedUser -> {
                                cacheService.removeUser(updatedUser.getId());
                                userEventPublisher.publishUserUpdatedEvent(updatedUser);
                            });
                })
                ;
    }

    public Mono<KDAPUserEntity> getUserById(String id) {
        if (!fullAccess)
            return isKDAPUserAuthenticated(securityHelper.getKDAPUserAuthentication())
                    .flatMap(kdapUserAuthentication -> {
                        if (kdapUserAuthentication.getUserRole() != UserRole.ADMIN) {
                            if (!id.equals(kdapUserAuthentication.getId())) {
                                log.info("Access denied {}", kdapUserAuthentication.getUserRole().toString());
                                return Mono.error(new AccessDeniedException("Access denied for role " + kdapUserAuthentication.getUserRole().toString()));

                            }
                        }
                        //Attempt to return cached value. If not found return from database and cache the result
                        KDAPUserEntity user = cacheService.getUser(id);
                        if (user != null) {
                            return Mono.just(user);
                        }
                        return userRepo.getUserByID(id)
                                .doOnSuccess(cacheService::cacheUser);
                    })
                    ;
        log.info("Getting user by id {} as super user", id);
        //Attempt to return cached value. If not found return from database and cache the result
        KDAPUserEntity user = cacheService.getUser(id);
        if (user != null) {
            return Mono.just(user);
        }
        return userRepo.getUserByID(id).doOnSuccess(cacheService::cacheUser);
    }

    public Mono<Boolean> deleteUser(String userID) {
        if (!fullAccess)
            return isKDAPUserAuthenticated(securityHelper.getKDAPUserAuthentication())
                    .flatMap(kdapUserAuthentication -> {
                        if (kdapUserAuthentication.getUserRole() != UserRole.ADMIN) {
                            if (!userID.equals(kdapUserAuthentication.getId())) {
                                log.info("Access denied {}", kdapUserAuthentication.getUserRole().toString());
                                return Mono.error(new AccessDeniedException("Access denied for role " + kdapUserAuthentication.getUserRole().toString()));
                            }
                        }
                        return userRepo.deleteUserByID(userID).doOnSuccess(deleted -> {
                            if (deleted) {
                                cacheService.removeUser(userID);
                                userEventPublisher.publishUserDeletedEvent(userID);
                            }
                        });
                    })
                    ;
        log.info("Deleting user {} as super user", userID);
        return userRepo.deleteUserByID(userID).doOnSuccess(deleted -> {
            if (deleted) {
                cacheService.removeUser(userID);
                userEventPublisher.publishUserDeletedEvent(userID);
            }
        });
    }


    /**
     * Updates the property of user passed based on googleUserInfo
     */
    public KDAPUserEntity updateUserFromOAuthUserInfoGoogle(OAuth2UserInfoGoogle oAuth2UserInfoGoogle, KDAPUserEntity KDAPUserEntity) {
        if (!oAuth2UserInfoGoogle.getSub().equals(KDAPUserEntity.getId())) {
            throw new InputMismatchException("OAuth2UserInfo has sub " + oAuth2UserInfoGoogle.getSub() + " but userEntity has id " + KDAPUserEntity.getId());
        }
        KDAPUserEntity.setUpdated(LocalDate.now());
        KDAPUserEntity.setName(oAuth2UserInfoGoogle.getName());
        KDAPUserEntity.setPicture(oAuth2UserInfoGoogle.getPictureURL());
        KDAPUserEntity.setEmail(oAuth2UserInfoGoogle.getEmailID());
        return KDAPUserEntity;
    }


    private Mono<KDAPUserAuthentication> isKDAPUserAuthenticated(Mono<KDAPUserAuthentication> kdapUserAuthentication) {
        return kdapUserAuthentication.flatMap(kdapUserAuthentication1 -> {
            if (!kdapUserAuthentication1.isAuthenticated()) {
                log.info("KDAP User not authenticated");
                return Mono.empty();
            }
            return Mono.just(kdapUserAuthentication1);
        });
    }
}
