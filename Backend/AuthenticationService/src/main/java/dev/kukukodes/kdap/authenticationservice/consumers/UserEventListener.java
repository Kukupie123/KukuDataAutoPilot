package dev.kukukodes.kdap.authenticationservice.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.kukukodes.kdap.authenticationservice.constants.RabbitMQConst;
import dev.kukukodes.kdap.authenticationservice.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumes user events
 */
@Slf4j
@Component
public class UserEventListener {

    private final ObjectMapper objectMapper;

    public UserEventListener(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    //@RabbitListener(queues = RabbitMQConst.Queues.USER_UPDATED)
    public void onUserUpdated(String updatedUserJSON) throws JsonProcessingException {
        var user = objectMapper.readValue(updatedUserJSON, UserEntity.class);
        log.info("Updated user : {}. We don't really do anything tho lol.", user);
    }

}
