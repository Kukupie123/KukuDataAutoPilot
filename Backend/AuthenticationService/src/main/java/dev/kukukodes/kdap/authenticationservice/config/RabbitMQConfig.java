package dev.kukukodes.kdap.authenticationservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.user.updated}")
    private String userUpdatedQueueName;

    @Value("${rabbitmq.exchange.user}")
    private String userExchangeName;

    @Value("${rabbitmq.route.user.updated}")
    private String userUpdatedRouteKey;

    @Value("${rabbitmq.route.user.added}")
    private String userAddedRouteKey;

    @Value("${rabbitmq.route.user.deleted}")
    private String userDeletedRouteKey;

    @Bean
    public DirectExchange userEventsExchange() {
        return new DirectExchange(userExchangeName);
    }

    @Bean
    public Queue userUpdatedQueue() {
        return QueueBuilder.durable(userUpdatedQueueName)
                .maxLength(1L)
                .build();
    }

    @Bean
    public Queue userAddedQueue() {
        return QueueBuilder.durable(userUpdatedQueueName.replace("updated", "added"))
                .build();
    }

    @Bean
    public Queue userDeletedQueue() {
        return QueueBuilder.durable(userUpdatedQueueName.replace("updated", "deleted"))
                .build();
    }

    @Bean
    public Binding userUpdatedBinding() {
        return BindingBuilder
                .bind(userUpdatedQueue())
                .to(userEventsExchange())
                .with(userUpdatedRouteKey);
    }

    @Bean
    public Binding userAddedBinding() {
        return BindingBuilder
                .bind(userAddedQueue())
                .to(userEventsExchange())
                .with(userAddedRouteKey);
    }

    @Bean
    public Binding userDeletedBinding() {
        return BindingBuilder
                .bind(userDeletedQueue())
                .to(userEventsExchange())
                .with(userDeletedRouteKey);
    }
}
