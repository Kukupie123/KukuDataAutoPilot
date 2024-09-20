package dev.kukukodes.kdap.authenticationservice.publishers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.kukukodes.kdap.authenticationservice.constants.RabbitMQConst;
import dev.kukukodes.kdap.authenticationservice.entity.UserEntity;
import dev.kukukodes.kdap.authenticationservice.helpers.RabbitMQHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Publishes user event
 */
@Slf4j
@Service
public class UserEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQHelper rabbitMQHelper;

    @Value("${spring.application.name}")
    private String applicationName;

    public UserEventPublisher(@Autowired RabbitTemplate rabbitTemplate, RabbitMQHelper rabbitMQHelper) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitMQHelper = rabbitMQHelper;
    }

    public void publishUserUpdateMsg(UserEntity updatedUser) throws JsonProcessingException {
        String routingKey = RabbitMQConst.Routes.USER_UPDATED;
        ObjectMapper objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();

        String userJSON = rabbitMQHelper.convertObjectsToJSON(updatedUser);
        log.info("Sending message to routing key : {}", userJSON);
        rabbitTemplate.convertAndSend(routingKey, userJSON);
    }
}
