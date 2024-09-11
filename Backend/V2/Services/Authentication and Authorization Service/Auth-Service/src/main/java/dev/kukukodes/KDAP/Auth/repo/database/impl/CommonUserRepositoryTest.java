package dev.kukukodes.KDAP.Auth.repo.database.impl;

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
@Profile("test")
public class CommonUserRepositoryTest implements IUserRepository {
    @Autowired
    private R2dbcEntityTemplate template;

    CommonUserRepositoryTest() {
        log.info("Using Test User Repository");
    }


    @Override
    public Mono<Void> addUser(UserEntity user) {
        return template.insert(user).then();
    }

    @Override
    public Mono<UserEntity> getUserById(int id) {
        log.info("getUserById {}", id);
        return template.
                select(
                        UserEntity.class
                ).matching(
                        Query.query(
                                Criteria.where("id").is(id)
                        )
                ).one()
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("Found no user in table with id {}", id);
                    return Mono.empty();
                }));
    }

    public Mono<UserEntity> getUserByUserId(String userId) {
        return template
                .select(UserEntity.class)
                .matching(
                        Query.query(
                                Criteria.where("userID").is(userId)
                        )
                )
                .first()
                .switchIfEmpty(Mono.error(new AccountNotFoundException("User with id " + userId + " not found")))
                .doOnEach(userDbLevelSignal -> log.info("Found user in table {}", userDbLevelSignal.get()));
    }

    @Override
    public Flux<UserEntity> getAllUsers() {
        return template.select(UserEntity.class).all().doOnNext(user -> log.info("User found: {}", user));
    }
}
