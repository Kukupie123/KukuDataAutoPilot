package dev.kukukodes.KDAP.Auth.repo.database;

import dev.kukukodes.KDAP.Auth.entities.database.UserEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repository interface for interacting with the 'users' table.
 */
public interface IUserRepository {
    Mono<Integer> addUser(UserEntity user);
    Mono<UserEntity> getUserById(int id);
    Mono<UserEntity> getUserByName(String userId);
    Flux<UserEntity> getAllUsers();

}
