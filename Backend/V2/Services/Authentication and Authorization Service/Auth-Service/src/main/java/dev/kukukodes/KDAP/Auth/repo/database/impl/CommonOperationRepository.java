package dev.kukukodes.KDAP.Auth.repo.database.impl;

import dev.kukukodes.KDAP.Auth.entities.database.OperationEntity;
import dev.kukukodes.KDAP.Auth.repo.database.IOperationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class CommonOperationRepository implements IOperationRepository {
    @Autowired
    R2dbcEntityTemplate template;

    @Override
    public Mono<Integer> addOperation(OperationEntity operationEntity) {
        return template.insert(operationEntity).map(OperationEntity::getId);
    }
}
