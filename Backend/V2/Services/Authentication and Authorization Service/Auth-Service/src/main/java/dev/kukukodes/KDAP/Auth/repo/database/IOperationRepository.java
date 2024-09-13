package dev.kukukodes.KDAP.Auth.repo.database;

import dev.kukukodes.KDAP.Auth.entities.database.OperationEntity;
import reactor.core.publisher.Mono;

public interface IOperationRepository {
    ///Returns ID of the added operation
    Mono<Integer> addOperation(OperationEntity operationEntity);
}
