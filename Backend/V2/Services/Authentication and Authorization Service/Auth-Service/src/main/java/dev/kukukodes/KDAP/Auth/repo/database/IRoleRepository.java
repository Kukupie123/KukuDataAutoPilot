package dev.kukukodes.KDAP.Auth.repo.database;

import dev.kukukodes.KDAP.Auth.entities.database.RoleEntity;
import reactor.core.publisher.Mono;

public interface IRoleRepository extends IBaseRepository<RoleEntity, Integer> {
    Mono<RoleEntity> getRoleByName(String name);
}
