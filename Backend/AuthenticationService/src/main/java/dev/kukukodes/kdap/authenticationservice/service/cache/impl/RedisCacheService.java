package dev.kukukodes.kdap.authenticationservice.service.cache.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.kukukodes.kdap.authenticationservice.service.cache.ICacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Service
public class RedisCacheService implements ICacheService {

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisCacheService(ReactiveRedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public <T> Mono<Boolean> put(String key, T obj, Duration duration) throws JsonProcessingException {
        log.info("Adding data to cache {}:{}", key, obj);
        if (duration == null) {
            duration = Duration.ofMinutes(5);
        }
        String json = objectMapper.writeValueAsString(obj);
        log.info("Converted to json : {}", json);
        return redisTemplate.opsForValue().set(key, json, duration);
    }

    @Override
    public <T> Mono<T> get(String key, Class<T> objType) {
        log.info("Getting item from cache of key {}", key);
        return redisTemplate.opsForValue().get(key)
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("No item found in cache with key {}", key);
                    return Mono.empty();
                }))
                .flatMap(json -> {
                    try {
                        T obj = objectMapper.readValue(json, objType);
                        return Mono.just(obj);
                    } catch (JsonProcessingException e) {
                        return Mono.error(e);
                    }
                }).doOnNext(t -> log.info("Found item : {}", t.toString()));
    }
}
