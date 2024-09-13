package dev.kukukodes.KDAP.Auth.repo.database.impl;

import dev.kukukodes.KDAP.Auth.constants.database.DbConstants;
import dev.kukukodes.KDAP.Auth.entities.database.RoleEntity;
import dev.kukukodes.KDAP.Auth.repo.database.IRoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
public class CommonRoleRepository implements IRoleRepository {

    @Autowired
    R2dbcEntityTemplate template;

    @Override
    public Mono<Integer> addRole(RoleEntity role) {
        log.info("Adding new role {}", role);
        return template.insert(role).map(RoleEntity::getId);
    }

    @Override
    public Mono<RoleEntity> getRoleByID(int id) {
        Integer val = id;
        return template.select(RoleEntity.class).matching(
                Query.query(
                        Criteria.where(DbConstants.TableColumnNames.CommonColumns.id).is(val)
                )).first();
    }

    @Override
    public Mono<RoleEntity> getRoleByName(String userId) {
        return template.select(RoleEntity.class).matching(
                Query.query(
                        Criteria.where(DbConstants.TableColumnNames.CommonColumns.name).is(userId)
                )
        ).first();
    }

    @Override
    public Flux<RoleEntity> getAllRoles() {
        return template.select(RoleEntity.class).all();
    }
}
