package dev.kukukodes.kdap.authenticationservice.config;

import dev.kukukodes.kdap.authenticationservice.constants.RabbitMQConst;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    /**
     * Direct Exchange sends message to all queues routed to the bound routing key
     */
    @Bean
    public DirectExchange userEventsExchange() {
        return new DirectExchange(RabbitMQConst.Exchanges.getUserExchange());
    }

    /**
     * Internal Queue used that stores the latest updated user, hence why it's length is 1.
     * It has {spring.application.name} attached to it's start to make sure it's name isn't shared with other queues.
     * This is to ensure that no other service accidentally consume its message.
     */
    @Bean
    public Queue userUpdatedQueue() {
        return QueueBuilder.durable(RabbitMQConst.Queues.getUserUpdatedQueue())
                .maxLength(1L)
                .build();
    }

    @Bean
    public Binding binding() {
        return BindingBuilder
                .bind(userUpdatedQueue())
                .to(userEventsExchange())
                .with(RabbitMQConst.Routes.getUserUpdatedRoute());
    }
}
