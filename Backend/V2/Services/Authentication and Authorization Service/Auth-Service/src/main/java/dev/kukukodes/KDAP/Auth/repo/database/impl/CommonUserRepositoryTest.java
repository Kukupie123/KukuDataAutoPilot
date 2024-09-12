package dev.kukukodes.KDAP.Auth.repo.database.impl;

import dev.kukukodes.KDAP.Auth.constants.database.DbConstants;
import dev.kukukodes.KDAP.Auth.entities.database.UserEntity;
import dev.kukukodes.KDAP.Auth.repo.database.IUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.security.auth.login.AccountNotFoundException;

/**
 * Concrete implementation of UserRepository.
 */
@Repository
@Slf4j
public class CommonUserRepositoryTest implements IUserRepository {
    @Autowired
    private R2dbcEntityTemplate template;

    @Override
    public Mono<Integer> addUser(UserEntity user) {
        user.setId(null);
        return template.insert(user).map(UserEntity::getId);
    }

    @Override
    public Mono<UserEntity> getUserById(int id) {
        log.info("getUserById {}", id);
        return template.
                select(UserEntity.class).matching(
                        Query.query(
                                Criteria.where(DbConstants.TableColumnNames.CommonColumns.id).is(id)
                        )
                ).one();
    }

    public Mono<UserEntity> getUserByName(String userId) {
        return template
                .select(UserEntity.class)
                .matching(
                        Query.query(
                                Criteria.where(DbConstants.TableColumnNames.CommonColumns.name).is(userId)
                        )
                )
                .first();
    }

    @Override
    public Flux<UserEntity> getAllUsers() {
        return template.select(UserEntity.class).all();
    }
}
