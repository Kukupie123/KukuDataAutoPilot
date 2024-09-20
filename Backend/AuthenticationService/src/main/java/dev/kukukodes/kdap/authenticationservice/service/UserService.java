package dev.kukukodes.kdap.authenticationservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.kukukodes.kdap.authenticationservice.dto.user.UserJwtClaimsDTO;
import dev.kukukodes.kdap.authenticationservice.entity.UserEntity;
import dev.kukukodes.kdap.authenticationservice.models.OAuth2UserInfoGoogle;
import dev.kukukodes.kdap.authenticationservice.publishers.UserEventPublisher;
import dev.kukukodes.kdap.authenticationservice.repo.IUserRepo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ClaimsBuilder;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class UserService {
    private final IUserRepo userRepo;

    private final UserEventPublisher userEventPublisher;

    UserService(IUserRepo userRepo, UserEventPublisher userEventPublisher) {
        this.userRepo = userRepo;
        this.userEventPublisher = userEventPublisher;
    }

    public Mono<UserEntity> addUser(UserEntity user) {
        return userRepo.addUser(user);
    }

    @CacheEvict(value = "user", key = "#user.id")
    public Mono<UserEntity> updateUser(UserEntity user) {
        return userRepo.updateUser(user).doOnSuccess(updatedUser -> {
            try {
                userEventPublisher.publishUserUpdateMsg(updatedUser);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @CacheEvict(value = "user", key = "#user.id")
    public Mono<UserEntity> AddUpdateUser(UserEntity user) {
        return userRepo.getUserByID(user.getId())
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("No existing user found. Adding new one");
                    return userRepo.addUser(user);
                }))
                .flatMap(userEntity -> {
                    log.info("Existing user found. Updating user");
                    return userRepo.updateUser(userEntity);
                })
                ;
    }

    @Cacheable(value = "user", key = "#id")
    public Mono<UserEntity> getUserById(String id) {
        log.info("Getting user {} from database", id);
        return userRepo.getUserByID(id);
    }

    /**
     * Create claim with id as subject
     */
    public Claims createClaimsForUser(UserEntity user) {
        return Jwts.claims().subject(user.getId()).build();
    }

    public Mono<UserEntity> getUserByJwtClaimsDTO(UserJwtClaimsDTO userJwtClaimsDTO) {
        return userRepo.getUserByID(userJwtClaimsDTO.getId());
    }

    /**
     * Updates the property of user passed based on googleUserInfo
     */
    public UserEntity updateUserFromOAuthUserInfoGoogle(OAuth2UserInfoGoogle oAuth2UserInfoGoogle, UserEntity userEntity) {
        userEntity.setName(oAuth2UserInfoGoogle.getName());
        userEntity.setPicture(oAuth2UserInfoGoogle.getPictureURL());
        userEntity.setEmail(oAuth2UserInfoGoogle.getEmailID());
        return userEntity;
    }
}
