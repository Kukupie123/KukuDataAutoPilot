package dev.kukukodes.kdap.authenticationservice.service.cache.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisCacheServiceUnitTest {

    // Constants
    private final String key = "testKey";
    private final TestObject testObject = new TestObject("test", 123);
    private final String json = "{\"name\":\"test\",\"value\":123}";

    @Mock
    private ReactiveRedisTemplate<String, String> redisTemplate;

    @Mock
    private ReactiveValueOperations<String, String> reactiveValueOperations;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private RedisCacheService redisCacheService;

    @BeforeEach
    void setUp() {
        // Set up the opsForValue() mock, as it's used in both tests
        when(redisTemplate.opsForValue()).thenReturn(reactiveValueOperations);
    }

    @Test
    void put_shouldStoreObjectInCache() throws JsonProcessingException {
        // Arrange
        when(objectMapper.writeValueAsString(testObject)).thenReturn(json);
        when(reactiveValueOperations.set(eq(key), eq(json), any(Duration.class))).thenReturn(Mono.just(true));

        // Act
        Mono<Boolean> result = redisCacheService.put(key, testObject, Duration.ofMinutes(5));

        // Assert
        assertThat(result.block()).isTrue();

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Duration> durationCaptor = ArgumentCaptor.forClass(Duration.class);

        verify(reactiveValueOperations).set(keyCaptor.capture(), valueCaptor.capture(), durationCaptor.capture());
        assertThat(keyCaptor.getValue()).isEqualTo(key);
        assertThat(valueCaptor.getValue()).isEqualTo(json);
        assertThat(durationCaptor.getValue()).isEqualTo(Duration.ofMinutes(5));
    }

    @Test
    void get_shouldReturnObjectFromCache() throws JsonProcessingException {
        // Arrange
        when(reactiveValueOperations.get(eq(key))).thenReturn(Mono.just(json));
        when(objectMapper.readValue(json, TestObject.class)).thenReturn(testObject);

        // Act
        Mono<TestObject> result = redisCacheService.get(key, TestObject.class);

        // Assert
        assertThat(result.block()).isEqualTo(testObject);
        verify(reactiveValueOperations).get(key);
        verify(objectMapper).readValue(json, TestObject.class);
    }

    @Getter
    static class TestObject {
        private final String name;
        private final int value;

        public TestObject(String name, int value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public String toString() {
            return "TestObject{name='" + name + "', value=" + value + '}';
        }
    }
}
