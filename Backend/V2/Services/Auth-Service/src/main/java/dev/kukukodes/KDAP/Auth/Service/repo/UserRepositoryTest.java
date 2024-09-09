package dev.kukukodes.KDAP.Auth.Service.repo;

import dev.kukukodes.KDAP.Auth.Service.entity.UserDbLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Concrete implementation of UserRepository.
 */
@Repository
@Profile("test")
public class UserRepositoryTest implements IUserRepository {
    final Logger log = LoggerFactory.getLogger(UserRepositoryTest.class);
    @Autowired
    private R2dbcEntityTemplate template;

    UserRepositoryTest() {
        log.info("Using Test User Repository");
    }


    @Override
    public Mono<UserDbLevel> getUserById(int id) {
        return template.
                select(
                        UserDbLevel.class
                ).matching(
                        Query.query(
                                Criteria.where("id").is(id)
                        )
                ).one();
    }

    @Override
    public Flux<UserDbLevel> getUsersByUserId(String userId) {
        return template.
                select(UserDbLevel.class).matching(
                        Query.query(
                                Criteria.where("userID").is(userId)
                        )).all();
    }
}
