package dev.kukukodes.kdap.authenticationservice.service;

import dev.kukukodes.kdap.authenticationservice.entity.UserEntity;
import dev.kukukodes.kdap.authenticationservice.enums.UserRole;
import dev.kukukodes.kdap.authenticationservice.helpers.SecurityHelper;
import dev.kukukodes.kdap.authenticationservice.models.KDAPUserAuthentication;
import dev.kukukodes.kdap.authenticationservice.models.OAuth2UserInfoGoogle;
import dev.kukukodes.kdap.authenticationservice.publishers.UserEventPublisher;
import dev.kukukodes.kdap.authenticationservice.repo.IUserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.InputMismatchException;

@Slf4j
@Service
public class UserService {
    private final IUserRepo userRepo;
    private final CacheService cacheService;
    private final SecurityHelper securityHelper;

    private final UserEventPublisher userEventPublisher;

    UserService(IUserRepo userRepo, CacheService cacheService, SecurityHelper securityHelper, UserEventPublisher userEventPublisher) {
        this.userRepo = userRepo;
        this.cacheService = cacheService;
        this.securityHelper = securityHelper;
        this.userEventPublisher = userEventPublisher;
    }

    /**
     * Adds a new user to database. ONLY ADMIN can do it
     */
    public Mono<UserEntity> addUser(UserEntity userToAdd) {
        return isKDAPUserAuthenticated(securityHelper.getKDAPUserAuthentication())
                .flatMap(kdapUserAuthentication -> {
                    if (kdapUserAuthentication.getUserRole() != UserRole.ADMIN) {
                        log.info("Access denied {}", kdapUserAuthentication.getUserRole().toString());
                        return Mono.error(new AccessDeniedException("Access denied for role " + kdapUserAuthentication.getUserRole().toString()));
                    }
                    return userRepo.addUser(userToAdd);
                })
                ;
        //TODO: Publish user added event
    }

    /**
     * Update existing user
     */
    public Mono<UserEntity> updateUser(UserEntity user) {
        return isKDAPUserAuthenticated(securityHelper.getKDAPUserAuthentication())
                // Needs to be admin or else only allowed to update self info
                .flatMap(kdapUserAuthentication -> {
                    if (kdapUserAuthentication.getUserRole() != UserRole.ADMIN) {
                        if (!user.getId().equals(kdapUserAuthentication.getId())) {
                            log.info("Access denied {}", kdapUserAuthentication.getUserRole().toString());
                            return Mono.error(new AccessDeniedException("Access denied for role " + kdapUserAuthentication.getUserRole().toString()));
                        }
                    }
                    return Mono.just(kdapUserAuthentication);
                })
                //get user from database
                .flatMap(kdapUserAuthentication -> userRepo.getUserByID(kdapUserAuthentication.getId()))
                //compare the values to determine if they are different and update if not same
                .flatMap(dbUser -> {
                    if (
                            user.getId().equals(dbUser.getId()) &&
                                    user.getEmail().equals(dbUser.getEmail()) &&
                                    user.getPassword().equals(dbUser.getPassword()) &&
                                    user.getPicture().equals(dbUser.getPicture())
                    ) {
                        log.warn("Nothing to update");
                        return Mono.just(dbUser);
                    }
                    log.info("Updating user info from {} to {}", dbUser, user);
                    dbUser.setUpdated(LocalDate.now());
                    dbUser.setPassword(dbUser.getPassword());
                    dbUser.setPicture(dbUser.getPicture());
                    dbUser.setEmail(dbUser.getEmail());
                    dbUser.setName(dbUser.getName());
                    return userRepo.updateUser(dbUser)
                            .doOnSuccess(user1 -> cacheService.removeUser(user1.getId()));
                })
                ;
        //TODO: Publish event
    }

    public Mono<UserEntity> getUserById(String id) {

        return isKDAPUserAuthenticated(securityHelper.getKDAPUserAuthentication())
                .flatMap(kdapUserAuthentication -> {
                    if (kdapUserAuthentication.getUserRole() != UserRole.ADMIN) {
                        if (!id.equals(kdapUserAuthentication.getId())) {
                            log.info("Access denied {}", kdapUserAuthentication.getUserRole().toString());
                            return Mono.error(new AccessDeniedException("Access denied for role " + kdapUserAuthentication.getUserRole().toString()));

                        }
                    }
                    return userRepo.getUserByID(id)
                            .doOnSuccess(cacheService::cacheUser);
                })
                ;
    }

    public Mono<Boolean> deleteUser(String userID) {
        //TODO: publish event and cache evict
        return isKDAPUserAuthenticated(securityHelper.getKDAPUserAuthentication())
                .flatMap(kdapUserAuthentication -> {
                    if (kdapUserAuthentication.getUserRole() != UserRole.ADMIN) {
                        if (!userID.equals(kdapUserAuthentication.getId())) {
                            log.info("Access denied {}", kdapUserAuthentication.getUserRole().toString());
                            return Mono.error(new AccessDeniedException("Access denied for role " + kdapUserAuthentication.getUserRole().toString()));
                        }
                    }
                    return userRepo.deleteUserByID(userID).doOnSuccess(deleted -> {
                        if (deleted)
                            cacheService.removeUser(userID);
                    });
                })
                ;
    }


    /**
     * Updates the property of user passed based on googleUserInfo
     */
    public UserEntity updateUserFromOAuthUserInfoGoogle(OAuth2UserInfoGoogle oAuth2UserInfoGoogle, UserEntity userEntity) {
        if (!oAuth2UserInfoGoogle.getSub().equals(userEntity.getId())) {
            throw new InputMismatchException("OAuth2UserInfo has sub " + oAuth2UserInfoGoogle.getSub() + " but userEntity has id " + userEntity.getId());
        }
        userEntity.setUpdated(LocalDate.now());
        userEntity.setName(oAuth2UserInfoGoogle.getName());
        userEntity.setPicture(oAuth2UserInfoGoogle.getPictureURL());
        userEntity.setEmail(oAuth2UserInfoGoogle.getEmailID());
        return userEntity;
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
