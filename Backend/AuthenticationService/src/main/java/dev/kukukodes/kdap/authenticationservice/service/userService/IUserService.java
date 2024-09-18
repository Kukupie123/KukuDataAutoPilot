package dev.kukukodes.kdap.authenticationservice.service.userService;

import dev.kukukodes.kdap.authenticationservice.entity.UserEntity;
import reactor.core.publisher.Mono;

public interface IUserService {
    Mono<UserEntity> addUser(UserEntity user);

    Mono<UserEntity> updateUser(UserEntity user);

    Mono<UserEntity> AddUpdateUser(UserEntity user);

    Mono<UserEntity> getUserById(String id);
}
