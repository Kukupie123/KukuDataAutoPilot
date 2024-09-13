package dev.kukukodes.KDAP.Auth.repo.database;

import dev.kukukodes.KDAP.Auth.entities.database.PermissionEntity;
import reactor.core.publisher.Mono;


public interface IPermissionRepository  extends IBaseRepository<PermissionEntity,Integer>{
    Mono<Integer> getPermissionByName(PermissionEntity permission);
}
