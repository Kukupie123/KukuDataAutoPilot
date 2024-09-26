package dev.kukukodes.kdap.authenticationservice.repo;

import dev.kukukodes.kdap.authenticationservice.constants.AccessLevelConst;
import dev.kukukodes.kdap.authenticationservice.entity.user.KDAPUserEntity;
import dev.kukukodes.kdap.authenticationservice.helpers.SecurityHelper;
import io.r2dbc.spi.ConnectionFactory;
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
public class UserRepo implements IUserRepo {
    private final R2dbcEntityTemplate template;
    private final SecurityHelper securityHelper;

    public UserRepo(@Autowired ConnectionFactory connectionFactory, SecurityHelper securityHelper) {
        this.template = new R2dbcEntityTemplate(connectionFactory);
        this.securityHelper = securityHelper;
    }

    @Override
    public Mono<KDAPUserEntity> addUser(KDAPUserEntity user) {
        log.info("Adding user to db {}", user);
        return template.insert(user);
    }

    @Override
    public Mono<KDAPUserEntity> updateUser(KDAPUserEntity user) {
        log.info("Updating user to db {}", user);
        return template.update(user);
    }

    @Override
    public Mono<KDAPUserEntity> getUserByID(String id) {
        log.info("Getting user from DB {}", id);
        return template.selectOne(Query.query(Criteria.where("id").is(id)), KDAPUserEntity.class)
                .doOnNext(this::setAccessLevelOfUser)
                ;
    }

    @Override
    public Mono<Boolean> deleteUserByID(String id) {
        log.info("Deleting user from DB {}", id);
        return template.delete(Query.query(Criteria.where("id").is(id)), KDAPUserEntity.class)
                .map(deleted -> deleted > 0);
    }

    @Override
    public Flux<KDAPUserEntity> getAllUsers(int skip, int limit) {
        log.info("Getting all users from db");
        return template.select(KDAPUserEntity.class)
                .matching(
                        Query.empty().offset(skip).limit(limit)
                ).all()
                .doOnNext(this::setAccessLevelOfUser)
                ;
    }

    private void setAccessLevelOfUser(KDAPUserEntity user) {
        if (securityHelper.isSuperuser(user.getId())) {
            user.setAccessLevel(AccessLevelConst.ADMIN);
            return;
        }
        user.setAccessLevel(AccessLevelConst.SELF);
    }
}
