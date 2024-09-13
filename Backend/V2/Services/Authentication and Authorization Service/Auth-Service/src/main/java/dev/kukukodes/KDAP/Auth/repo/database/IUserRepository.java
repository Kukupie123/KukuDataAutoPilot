package dev.kukukodes.KDAP.Auth.repo.database;

import dev.kukukodes.KDAP.Auth.entities.database.UserEntity;
import reactor.core.publisher.Mono;

/**
 * Repository interface for interacting with the 'users' table.
 */
public interface IUserRepository extends  IBaseRepository<UserEntity,Integer> {
    Mono<UserEntity> getUserByName(String username);
}
