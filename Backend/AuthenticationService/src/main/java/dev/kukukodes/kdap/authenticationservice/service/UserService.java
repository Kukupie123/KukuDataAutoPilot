package dev.kukukodes.kdap.authenticationservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.kukukodes.kdap.authenticationservice.entity.UserEntity;
import dev.kukukodes.kdap.authenticationservice.models.OAuth2UserInfoGoogle;
import dev.kukukodes.kdap.authenticationservice.publishers.UserEventPublisher;
import dev.kukukodes.kdap.authenticationservice.repo.IUserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.InputMismatchException;

@Slf4j
@Service
public class UserService {
    private final IUserRepo userRepo;
    private final CacheService cacheService;


    private final UserEventPublisher userEventPublisher;

    UserService(IUserRepo userRepo, CacheService cacheService, UserEventPublisher userEventPublisher) {
        this.userRepo = userRepo;
        this.cacheService = cacheService;
        this.userEventPublisher = userEventPublisher;
    }

    /**
     * Adds a new user to database
     */
    public Mono<UserEntity> addUser(UserEntity user) {
        return userRepo.addUser(user);
    }

    /**
     * Update existing user
     */
    public Mono<UserEntity> updateUser(UserEntity user) {
        return userRepo.updateUser(user)
                .doOnSuccess(updatedUser -> {
                    try {
                        cacheService.removeUser(user.getId());
                        userEventPublisher.publishUserUpdateMsg(updatedUser);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    /**
     * Adds or Updates user if it exists.
     */
    public Mono<UserEntity> AddUpdateUser(UserEntity user) {
        return userRepo.getUserByID(user.getId())
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("No existing user found. Adding new one");
                    return addUser(user);
                }))
                .flatMap(userEntity -> {
                    log.info("Existing user found. Updating user");
                    cacheService.removeUser(user.getId());
                    return userRepo.updateUser(userEntity);
                })
                ;
    }

    public Mono<UserEntity> getUserById(String id) {
        log.info("Getting user {}", id);

        UserEntity cachedUser = cacheService.getUser(id);

        if (cachedUser != null) {
            log.info("User {} found in cache", id);
            return Mono.just(cachedUser);
        }

        return userRepo.getUserByID(id)
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("User not found {}", id);
                    return Mono.empty();
                }))
                .doOnSuccess(user -> {
                    if (user != null) {
                        log.info("Caching user {}", id);
                        cacheService.cacheUser(user);
                    }
                });
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

    public Mono<Boolean> deleteUser(String userID) {
        log.info("Deleting user {}", userID);
        cacheService.removeUser(userID);
        return userRepo.deleteUserByID(userID);
    }
}
