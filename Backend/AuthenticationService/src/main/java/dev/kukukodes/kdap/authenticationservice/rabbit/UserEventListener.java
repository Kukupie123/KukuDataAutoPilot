package dev.kukukodes.kdap.authenticationservice.rabbit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.kukukodes.kdap.authenticationservice.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserEventListener {

    private final ObjectMapper objectMapper;

    public UserEventListener(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "user.updated")
    public void onUserUpdated(String updatedUserJSON) throws JsonProcessingException {
        var user = objectMapper.readValue(updatedUserJSON, UserEntity.class);
        log.info("Updated user : {}", user);
    }

}
