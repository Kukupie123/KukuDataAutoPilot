package dev.kukukodes.KDAP.Auth.repo.database.impl;

import dev.kukukodes.KDAP.Auth.constants.auth.AuthConstants;
import dev.kukukodes.KDAP.Auth.constants.database.DbConstants;
import dev.kukukodes.KDAP.Auth.entities.database.UserEntity;
import dev.kukukodes.KDAP.Auth.repo.database.IUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Concrete implementation of UserRepository.
 */
@Repository
@Slf4j
public class CommonUserRepository implements IUserRepository {
    @Autowired
    private R2dbcEntityTemplate template;


    @Override
    public Mono<Integer> add(UserEntity userEntity) {
        log.info("Adding user {}", userEntity);
        return template.insert(userEntity).map(UserEntity::getId);
    }

    @Override
    public Mono<UserEntity> getByPK(Integer id) {
        log.info("Getting user by PK {}", id);
        return template.select(
                Query.query(
                        Criteria.where(DbConstants.TableColumnNames.CommonColumns.id).is(id)
                ),
                UserEntity.class).next();
    }

    @Override
    public Flux<UserEntity> getAll() {
        log.info("Getting all users");
        return template.select(UserEntity.class).all();
    }

    @Override
    public Mono<UserEntity> getUserByName(String username) {
        return template.select(
                Query.query(
                        Criteria.where(DbConstants.TableColumnNames.CommonColumns.name).is(username)
                ),
                UserEntity.class
        ).next();
    }
}
