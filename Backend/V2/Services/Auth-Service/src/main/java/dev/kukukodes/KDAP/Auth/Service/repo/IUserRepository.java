package dev.kukukodes.KDAP.Auth.Service.repo;

import dev.kukukodes.KDAP.Auth.Service.entity.UserDbLevel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repository interface for interacting with the 'users' table.
 */
public interface IUserRepository {
    Mono<UserDbLevel> getUserById(int id);
    Flux<UserDbLevel> getUsersByUserId(String userId);
}
