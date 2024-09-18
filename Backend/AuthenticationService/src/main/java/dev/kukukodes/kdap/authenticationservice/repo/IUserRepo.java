package dev.kukukodes.kdap.authenticationservice.repo;


import dev.kukukodes.kdap.authenticationservice.entity.UserEntity;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface IUserRepo{
    Mono<UserEntity> addUser(UserEntity user);
    Mono<UserEntity> updateUser(UserEntity user);
    Mono<UserEntity> getUserByID(String id);
}
