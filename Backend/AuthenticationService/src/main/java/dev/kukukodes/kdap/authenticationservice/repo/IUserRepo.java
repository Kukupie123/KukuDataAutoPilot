package dev.kukukodes.kdap.authenticationservice.repo;


import dev.kukukodes.kdap.authenticationservice.entity.user.KDAPUserEntity;
import reactor.core.publisher.Mono;

public interface IUserRepo{
    Mono<KDAPUserEntity> addUser(KDAPUserEntity user);
    Mono<KDAPUserEntity> updateUser(KDAPUserEntity user);
    Mono<KDAPUserEntity> getUserByID(String id);
    Mono<Boolean> deleteUserByID(String id);
}
