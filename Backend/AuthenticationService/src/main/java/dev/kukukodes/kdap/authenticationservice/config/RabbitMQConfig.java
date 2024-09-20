package dev.kukukodes.kdap.authenticationservice.config;

import dev.kukukodes.kdap.authenticationservice.constants.RabbitMQConst;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {


    @Bean
    public DirectExchange userEventsExchange() {
        return new DirectExchange(RabbitMQConst.Exchanges.getUserExchange());
    }

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
