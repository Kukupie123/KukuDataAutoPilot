package dev.kukukodes.kdap.authenticationservice.publishers;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.kukukodes.kdap.authenticationservice.entity.UserEntity;
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

    @Value("${rabbitmq.route.user.updated}")
    private String routingKey;

    public UserEventPublisher(@Autowired RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishUserUpdateMsg(UserEntity updatedUser) throws JsonProcessingException {
        log.info("Sending message to routing key : {}", updatedUser);
        rabbitTemplate.convertAndSend(routingKey, updatedUser);
    }
}
