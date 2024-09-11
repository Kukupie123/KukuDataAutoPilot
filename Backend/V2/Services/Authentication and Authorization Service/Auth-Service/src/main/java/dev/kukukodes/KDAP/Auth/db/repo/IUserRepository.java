package dev.kukukodes.KDAP.Auth.db.repo;

import dev.kukukodes.KDAP.Auth.db.Entities.UserEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repository interface for interacting with the 'users' table.
 */
public interface IUserRepository {
    Mono<Void> addUser(UserEntity user);
    Mono<UserEntity> getUserById(int id);
    Mono<UserEntity> getUserByUserId(String userId);
    Flux<UserEntity> getAllUsers();

}
