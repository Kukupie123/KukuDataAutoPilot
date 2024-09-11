package dev.kukukodes.KDAP.Auth.Service.db.repo;

import dev.kukukodes.KDAP.Auth.Service.db.entity.UserDbLevel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repository interface for interacting with the 'users' table.
 */
public interface IUserRepository {
    Mono<Void> addUser(UserDbLevel user);
    Mono<UserDbLevel> getUserById(int id);
    Mono<UserDbLevel> getUserByUserId(String userId);
    Flux<UserDbLevel> getAllUsers();

}
