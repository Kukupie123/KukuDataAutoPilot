package dev.kukukodes.KDAP.Auth.repo.database;

import dev.kukukodes.KDAP.Auth.entities.database.UserRoleJunctionEntity;
import reactor.core.publisher.Flux;

public interface IUserRoleJunctionRepository extends IBaseRepository<UserRoleJunctionEntity, UserRoleJunctionEntity.ID> {
    Flux<UserRoleJunctionEntity> getUserRoleJunctionsByUserId(int userId);
    Flux<UserRoleJunctionEntity> getUserRoleJunctionsByRoleId(int roleId);
}
