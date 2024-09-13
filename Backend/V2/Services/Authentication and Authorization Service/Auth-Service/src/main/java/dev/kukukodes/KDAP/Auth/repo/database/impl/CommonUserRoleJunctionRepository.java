package dev.kukukodes.KDAP.Auth.repo.database.impl;

import dev.kukukodes.KDAP.Auth.constants.database.DbConstants;
import dev.kukukodes.KDAP.Auth.entities.database.UserRoleJunctionEntity;
import dev.kukukodes.KDAP.Auth.repo.database.IUserRoleJunctionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
public class CommonUserRoleJunctionRepository implements IUserRoleJunctionRepository {
    @Autowired
    R2dbcEntityTemplate template;


    @Override
    public Mono<UserRoleJunctionEntity.ID> add(UserRoleJunctionEntity userRoleJunctionEntity) {
        log.info("Adding UserRoleJunction  {}", userRoleJunctionEntity);
        return template.insert(userRoleJunctionEntity).map(UserRoleJunctionEntity::getId);
    }

    @Override
    public Mono<UserRoleJunctionEntity> getByPK(UserRoleJunctionEntity.ID id) {
        log.info("Getting UserRoleJunction({})", id);
        return template.selectOne(Query.query(
                        Criteria.where(DbConstants.TableColumnNames.CommonColumns.id).is(id)),
                UserRoleJunctionEntity.class);
    }

    @Override
    public Flux<UserRoleJunctionEntity> getAll() {
        log.info("Getting every UserRoleJunction");
        return template.select(UserRoleJunctionEntity.class).all();
    }

    @Override
    public Flux<UserRoleJunctionEntity> getUserRoleJunctionsByUserId(int userId) {
        log.info("Getting user role junction with userID(){}", userId);
        return template.select(UserRoleJunctionEntity.class).matching(
                Query.query(
                        Criteria.where(DbConstants.TableColumnNames.CommonColumns.id).is(userId)
                )
        ).all();
    }

    @Override
    public Flux<UserRoleJunctionEntity> getUserRoleJunctionsByRoleId(int roleId) {
        log.info("Getting user role junction with roleID(){}", roleId);
        return template.select(UserRoleJunctionEntity.class).matching(
                Query.query(
                        Criteria.where(DbConstants.TableColumnNames.UserRolesJunctionTable.roleID).is(roleId)
                )
        ).all();
    }
}
