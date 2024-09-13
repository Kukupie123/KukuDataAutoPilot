package dev.kukukodes.KDAP.Auth.repo.database;

import dev.kukukodes.KDAP.Auth.entities.database.UserRoleJunctionEntity;
import reactor.core.publisher.Mono;

public interface IUserRoleJunctionRepository {
    Mono<UserRoleJunctionEntity> addJunction(int userID, int roleID);
}
