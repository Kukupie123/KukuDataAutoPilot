package dev.kukukodes.kdap.authenticationservice.config;

import dev.kukukodes.kdap.authenticationservice.constants.RabbitConst;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    //Exchange should represent a category of events
    //Queues should represent the type of event they are processing
    //Routing key should represent the type of event that its binding to

    @Value("${spring.application.name}")
    private String appName;

    @Bean(RabbitConst.Exchanges.UserEvent.EXCHANGE)
    public TopicExchange userEventsExchange() {
        return new TopicExchange(appName + "." + RabbitConst.Exchanges.UserEvent.EXCHANGE); //serviceName.user_event
    }

    /**
     * Queue used internally with max queue length as 1.
     * Queues are where messages are sent from exchanges based on.
     * Ensures that only 1 message is in the queue. If we get a second message the existing one is deleted.
     * We only need one queue because we store only the latest updated user as message
     */
    @Bean(RabbitConst.Exchanges.UserEvent.queueTypes.userUpdated)
    public Queue userUpdatedQueue() {
        return QueueBuilder.durable(appName + "." + RabbitConst.Exchanges.UserEvent.queueTypes.userUpdated) //Durable = message persists even when broker is down.
                .maxLength((long) 1)
                .build();
    }

    @Bean
    public Binding binding() {
        return BindingBuilder
                .bind(userUpdatedQueue())
                .to(userEventsExchange())
                .with(appName + "." + RabbitConst.Exchanges.UserEvent.queueTypes.userUpdated);
    }

}
