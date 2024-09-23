package dev.kukukodes.kdap.authenticationservice.repo;

import dev.kukukodes.kdap.authenticationservice.entity.UserEntity;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class UserRepo implements IUserRepo {
    private final R2dbcEntityTemplate template;

    public UserRepo(@Autowired ConnectionFactory connectionFactory) {
        this.template = new R2dbcEntityTemplate(connectionFactory);
    }

    @Override
    public Mono<UserEntity> addUser(UserEntity user) {
        return template.insert(user);
    }

    @Override
    public Mono<UserEntity> updateUser(UserEntity user) {
        return template.update(user);
    }

    @Override
    public Mono<UserEntity> getUserByID(String id) {
        return template.selectOne(Query.query(Criteria.where("id").is(id)), UserEntity.class);
    }

    @Override
    public Mono<Boolean> deleteUserByID(String id) {
        return template.delete(Query.query(Criteria.where("id").is(id)), UserEntity.class)
                .map(deleted -> deleted > 0);
    }
}
