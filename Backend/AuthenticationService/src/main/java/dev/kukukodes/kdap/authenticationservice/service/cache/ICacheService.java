package dev.kukukodes.kdap.authenticationservice.service.cache;


import com.fasterxml.jackson.core.JsonProcessingException;
import reactor.core.publisher.Mono;

import java.time.Duration;

public interface ICacheService {

    <T> Mono<Boolean> put(String key, T obj, Duration duration) throws JsonProcessingException;

    <T> Mono<T> get(String key, Class<T> objType);
}
