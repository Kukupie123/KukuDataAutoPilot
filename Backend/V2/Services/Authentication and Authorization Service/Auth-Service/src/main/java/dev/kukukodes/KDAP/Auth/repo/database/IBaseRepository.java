package dev.kukukodes.KDAP.Auth.repo.database;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Base repository for other repositories to extend from
 *
 * @param <Entity> The entity type the repository holds
 * @param <PK>     Primary key type
 * @param <SK>     Unique secondary "Primary Key". Eg "name unique not null" column.
 */
public interface IBaseRepository<Entity, PK> {
    Mono<PK> add(Entity entity);

    Mono<Entity> getByPK(PK id);
    Flux<Entity> getAll();
}
