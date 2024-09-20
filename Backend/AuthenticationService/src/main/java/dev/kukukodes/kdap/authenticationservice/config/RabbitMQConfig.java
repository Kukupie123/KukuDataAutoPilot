package dev.kukukodes.kdap.authenticationservice.config;

import dev.kukukodes.kdap.authenticationservice.constants.RabbitMQConst;
import dev.kukukodes.kdap.authenticationservice.helpers.RabbitMQHelper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {

    @Autowired
    RabbitMQHelper rabbitMQHelper;

    /**
     * Direct exchange is going to send the message to all queues which are bound to the routing key.
     * It has to be exact match
     */
    @Bean()
    public DirectExchange userEventsExchange() {
        return new DirectExchange(RabbitMQConst.Exchanges.USER_EVENT);
    }

    /**
     * Internal Queue with max length 1.
     * Ensures that only 1 message is in the queue. If we get a second message the existing one is deleted.
     * We only need one queue because we store only the latest updated user as message
     */
    @Bean()
    public Queue userUpdatedQueue() {
        return QueueBuilder.durable(RabbitMQConst.Queues.UPDATED) //Durable = message persists even when broker is down.
                .maxLength(1L)
                .build();
    }

    @Bean
    public Binding binding() {
        return BindingBuilder
                .bind(userUpdatedQueue())
                .to(userEventsExchange())
                .with(RabbitMQConst.Routes.USER_UPDATED);
    }

}
