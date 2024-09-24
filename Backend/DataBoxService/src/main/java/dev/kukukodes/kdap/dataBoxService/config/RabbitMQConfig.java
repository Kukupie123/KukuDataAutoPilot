package dev.kukukodes.kdap.dataBoxService.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {

    // Injecting exchange name
    @Value("${rabbitmq.exchange.databox}")
    private String datastoreExchangeName;

    // Injecting queue names
    @Value("${rabbitmq.queue.databox.added}")
    private String datastoreAddedQueueName;

    @Value("${rabbitmq.queue.databox.updated}")
    private String datastoreUpdatedQueueName;

    @Value("${rabbitmq.queue.databox.deleted}")
    private String datastoreDeletedQueueName;

    // Injecting routing keys
    @Value("${rabbitmq.route.databox.added}")
    private String datastoreAddedRoutingKey;

    @Value("${rabbitmq.route.databox.updated}")
    private String datastoreUpdatedRoutingKey;

    @Value("${rabbitmq.route.databox.deleted}")
    private String datastoreDeletedRoutingKey;

    // Bean for the datastore exchange
    @Bean
    public DirectExchange dataStoreExchange() {
        return new DirectExchange(datastoreExchangeName);
    }

    // Bean for datastore added queue
    @Bean
    public Queue dataStoreAddedQueue() {
        return new Queue(datastoreAddedQueueName);
    }

    // Bean for datastore updated queue
    @Bean
    public Queue dataStoreUpdatedQueue() {
        return new Queue(datastoreUpdatedQueueName);
    }

    // Bean for datastore deleted queue
    @Bean
    public Queue dataStoreDeletedQueue() {
        return new Queue(datastoreDeletedQueueName);
    }

    // Binding for added queue
    @Bean
    public Binding bindingDataStoreAdded(Queue dataStoreAddedQueue, DirectExchange dataStoreExchange) {
        return BindingBuilder.bind(dataStoreAddedQueue).to(dataStoreExchange).with(datastoreAddedRoutingKey);
    }

    // Binding for updated queue
    @Bean
    public Binding bindingDataStoreUpdated(Queue dataStoreUpdatedQueue, DirectExchange dataStoreExchange) {
        return BindingBuilder.bind(dataStoreUpdatedQueue).to(dataStoreExchange).with(datastoreUpdatedRoutingKey);
    }

    // Binding for deleted queue
    @Bean
    public Binding bindingDataStoreDeleted(Queue dataStoreDeletedQueue, DirectExchange dataStoreExchange) {
        return BindingBuilder.bind(dataStoreDeletedQueue).to(dataStoreExchange).with(datastoreDeletedRoutingKey);
    }
}
