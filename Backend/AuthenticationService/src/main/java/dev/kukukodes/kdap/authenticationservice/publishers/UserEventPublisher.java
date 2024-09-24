package dev.kukukodes.kdap.authenticationservice.publishers;

import dev.kukukodes.kdap.authenticationservice.entity.user.KDAPUserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Publishes user events
 */
@Slf4j
@Service
public class UserEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.user}")
    private String userExchange;

    @Value("${rabbitmq.route.user.updated}")
    private String userUpdatedRoutingKey;

    @Value("${rabbitmq.route.user.added}")
    private String userAddedRoutingKey;

    @Value("${rabbitmq.route.user.deleted}")
    private String userDeletedRoutingKey;

    public UserEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishUserUpdatedEvent(KDAPUserEntity updatedUser) {
        log.info("Sending user updated event for user: {}", updatedUser);
        rabbitTemplate.convertAndSend(userExchange, userUpdatedRoutingKey, updatedUser);
    }

    public void publishUserAddedEvent(KDAPUserEntity newUser) {
        log.info("Sending user added event for user: {}", newUser);
        rabbitTemplate.convertAndSend(userExchange, userAddedRoutingKey, newUser);
    }

    public void publishUserDeletedEvent(String userID) {
        log.info("Sending user deleted event for user: {}", userID);
        rabbitTemplate.convertAndSend(userExchange, userDeletedRoutingKey, userID);
    }
}
