package dev.kukukodes.kdap.authenticationservice.repo;


import dev.kukukodes.kdap.authenticationservice.entity.UserEntity;
import reactor.core.publisher.Mono;

public interface IUserRepo{
    Mono<UserEntity> addUser(UserEntity user);
    Mono<UserEntity> updateUser(UserEntity user);
    Mono<UserEntity> getUserByID(String id);
}
