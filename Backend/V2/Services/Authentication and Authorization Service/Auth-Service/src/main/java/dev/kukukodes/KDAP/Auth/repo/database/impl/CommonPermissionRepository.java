package dev.kukukodes.KDAP.Auth.repo.database.impl;

import dev.kukukodes.KDAP.Auth.entities.database.PermissionEntity;
import dev.kukukodes.KDAP.Auth.repo.database.IPermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class CommonPermissionRepository implements IPermissionRepository {
    @Autowired
    R2dbcEntityTemplate template;
    //TODO: next

    @Override
    public Mono<Integer> getPermissionByName(PermissionEntity permission) {
        return null;
    }

    @Override
    public Mono<Integer> add(PermissionEntity permissionEntity) {
        return null;
    }

    @Override
    public Mono<PermissionEntity> getByPK(Integer id) {
        return null;
    }

    @Override
    public Flux<PermissionEntity> getAll() {
        return null;
    }
}
