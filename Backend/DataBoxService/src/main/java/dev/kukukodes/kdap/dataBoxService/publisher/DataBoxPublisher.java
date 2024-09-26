package dev.kukukodes.kdap.dataBoxService.publisher;

import dev.kukukodes.kdap.dataBoxService.entity.dataBox.DataBox;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DataBoxPublisher {
    private final RabbitTemplate template;

    // Correcting the exchange property name to match the config
    @Value("${rabbitmq.exchange.databox}")
    private String dataStoreExchange;

    // Correcting the routing key property names
    @Value("${rabbitmq.route.databox.updated}")
    private String updatedRouteKey;

    @Value("${rabbitmq.route.databox.added}")
    private String addedRouteKey;

    @Value("${rabbitmq.route.databox.deleted}")
    private String deletedRouteKey;

    public DataBoxPublisher(RabbitTemplate template) {
        this.template = template;
    }

    // Method to publish an updated event
    public void publishDataBoxUpdatedEvent(DataBox dataBox) {
        log.info("EVENT: Data box updated {}", dataBox);
        template.convertAndSend(dataStoreExchange, updatedRouteKey, dataBox);
    }

    // Method to publish an added event
    public void publishDataBoxAddedEvent(DataBox dataBox) {
            log.info("EVENT: Data box added {}", dataBox);
        template.convertAndSend(dataStoreExchange, addedRouteKey, dataBox);
    }

    // Method to publish a deleted event
    public void publishDataBoxDeletedEvent(DataBox dataBox) {
        log.info("EVENT: Data box deleted {}", dataBox);
        template.convertAndSend(dataStoreExchange, deletedRouteKey, dataBox);
    }
}
