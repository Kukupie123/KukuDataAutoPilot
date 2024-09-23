package dev.kukukodes.kdap.authenticationservice.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    private final String userUpdatedQueueName;
    private final String userExchangeName;
    private final String routeKeyName;

    public RabbitMQConfig(@Value("${rabbitmq.queue.user.updated}") String userQueueName, @Value("${rabbitmq.exchange.user}") String userExchangeName, @Value("${rabbitmq.route.user.updated}") String routeKeyName) {
        this.userUpdatedQueueName = userQueueName;
        this.userExchangeName = userExchangeName;
        this.routeKeyName = routeKeyName;
    }

    /**
     * Direct Exchange sends message to all queues routed to the bound routing key
     */
    @Bean
    public DirectExchange userEventsExchange() {
        return new DirectExchange(userExchangeName);
    }

    /**
     * Internal Queue used that stores the latest updated user, hence why it's length is 1.
     * It has {spring.application.name} attached to it's start to make sure it's name isn't shared with other queues.
     * This is to ensure that no other service accidentally consume its message.
     */
    @Bean
    public Queue userUpdatedQueue() {
        return QueueBuilder.durable(userUpdatedQueueName)
                .maxLength(1L)
                .build();
    }

    @Bean
    public Binding binding() {
        return BindingBuilder
                .bind(userUpdatedQueue())
                .to(userEventsExchange())
                .with(routeKeyName);
    }
}
