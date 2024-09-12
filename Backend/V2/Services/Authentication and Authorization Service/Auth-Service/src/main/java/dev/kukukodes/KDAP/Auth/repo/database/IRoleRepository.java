package dev.kukukodes.KDAP.Auth.repo.database;

import dev.kukukodes.KDAP.Auth.entities.database.RoleEntity;
import dev.kukukodes.KDAP.Auth.entities.database.UserEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IRoleRepository {
    /**
     * Add role to db
     * @param role role to add. ID is ignored
     * @return new ID
     */
    Mono<Integer> addRole(RoleEntity role);
    Mono<RoleEntity> getRoleByID(int id);
    Mono<RoleEntity> getRoleByName(String userId);
    Flux<RoleEntity> getAllRoles();
}
