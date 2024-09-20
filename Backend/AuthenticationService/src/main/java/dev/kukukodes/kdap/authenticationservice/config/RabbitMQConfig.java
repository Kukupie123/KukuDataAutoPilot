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

    @Bean()
    public TopicExchange userEventsExchange() {
        return new TopicExchange("user");
    }

    /**
     * Queue used internally with max queue length as 1.
     * Queues are where messages are sent from exchanges based on.
     * Ensures that only 1 message is in the queue. If we get a second message the existing one is deleted.
     * We only need one queue because we store only the latest updated user as message
     */
    @Bean()
    public Queue userUpdatedQueue() {
        return QueueBuilder.durable("user.updated") //Durable = message persists even when broker is down.
                .maxLength(1L)
                .build();
    }

    @Bean
    public Binding binding() {
        return BindingBuilder
                .bind(userUpdatedQueue())
                .to(userEventsExchange())
                .with("user.updated");
    }

}
