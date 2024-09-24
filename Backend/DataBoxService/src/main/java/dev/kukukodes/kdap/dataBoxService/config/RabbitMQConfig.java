package dev.kukukodes.kdap.dataBoxService.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange.dataStore}")
    private String dataStoreExchangeName;

    @Value("${rabbitmq.queue.dataStore.updated}")
    private String dataStoreUpdatedQueueName;

    @Value("${rabbitmq.route.dataStore.updated}")
    private String dataStoreUpdatedRoutingKey;

    @Value("${rabbitmq.route.dataStore.added}")
    private String dataStoreAddedRoutingKey;

    @Value("${rabbitmq.route.dataStore.deleted}")
    private String dataStoreDeletedRoutingKey;

    @Bean
    public DirectExchange dataStoreExchange() {
        return new DirectExchange(dataStoreExchangeName);
    }

    @Bean
    public Queue dataStoreUpdatedQueue() {
        return new Queue(dataStoreUpdatedQueueName);
    }

    @Bean
    public Queue dataStoreAddedQueue() {
        return new Queue(dataStoreUpdatedQueueName.replace("updated", "added"));
    }

    @Bean
    public Queue dataStoreDeletedQueue() {
        return new Queue(dataStoreUpdatedQueueName.replace("updated", "deleted"));
    }

    @Bean
    public Binding bindingDataStoreUpdated(Queue dataStoreUpdatedQueue, DirectExchange dataStoreExchange) {
        return BindingBuilder.bind(dataStoreUpdatedQueue).to(dataStoreExchange).with(dataStoreUpdatedRoutingKey);
    }

    @Bean
    public Binding bindingDataStoreAdded(Queue dataStoreAddedQueue, DirectExchange dataStoreExchange) {
        return BindingBuilder.bind(dataStoreAddedQueue).to(dataStoreExchange).with(dataStoreAddedRoutingKey);
    }

    @Bean
    public Binding bindingDataStoreDeleted(Queue dataStoreDeletedQueue, DirectExchange dataStoreExchange) {
        return BindingBuilder.bind(dataStoreDeletedQueue).to(dataStoreExchange).with(dataStoreDeletedRoutingKey);
    }
}
