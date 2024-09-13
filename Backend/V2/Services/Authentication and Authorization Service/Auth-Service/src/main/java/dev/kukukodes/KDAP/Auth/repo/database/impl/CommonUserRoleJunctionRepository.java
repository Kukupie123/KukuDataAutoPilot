package dev.kukukodes.KDAP.Auth.repo.database.impl;

import dev.kukukodes.KDAP.Auth.entities.database.UserRoleJunctionEntity;
import dev.kukukodes.KDAP.Auth.repo.database.IUserRoleJunctionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
public class CommonUserRoleJunctionRepository implements IUserRoleJunctionRepository {
    @Autowired
    R2dbcEntityTemplate template;

    @Override
    public Mono<UserRoleJunctionEntity> addJunction(int userID, int roleID) {
        log.info("Adding user({}) role({}) junction.", userID, roleID);
        Integer uid = userID;
        Integer rid = roleID;
        var userToAdd = new UserRoleJunctionEntity(
                new UserRoleJunctionEntity.ID(uid, rid)
        );
        return template.insert(userToAdd);
    }
}
