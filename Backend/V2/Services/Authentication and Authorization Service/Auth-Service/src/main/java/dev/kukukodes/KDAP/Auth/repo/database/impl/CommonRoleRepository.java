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
    public Mono<RoleEntity> getRoleByName(String name) {
        log.info("Getting role by name {}", name);
        return template.select(
                Query.query(
                        Criteria.where(DbConstants.TableColumnNames.CommonColumns.name).is(name)
                ),
                RoleEntity.class
        ).next();
    }

    @Override
    public Mono<Integer> add(RoleEntity roleEntity) {
        log.info("Adding role {}", roleEntity);
        return template.insert(roleEntity).map(RoleEntity::getId);
    }

    @Override
    public Mono<RoleEntity> getByPK(Integer id) {
        log.info("Getting role by id {}", id);
        return template.selectOne(
                Query.query(
                        Criteria.where(DbConstants.TableColumnNames.CommonColumns.id).is(id)
                ),
                RoleEntity.class
        );
    }

    @Override
    public Flux<RoleEntity> getAll() {
        log.info("Getting all roles");
        return template.select(RoleEntity.class).all();
    }
}
