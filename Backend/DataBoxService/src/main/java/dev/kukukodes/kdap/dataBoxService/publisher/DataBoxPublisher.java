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

    @Value("${rabbitmq.exchange.dataStore}")
    private String dataStoreExchange;

    @Value("${rabbitmq.route.dataStore.updated}")
    private String updatedRouteKey;

    @Value("${rabbitmq.route.dataStore.added}")
    private String addedRouteKey;

    @Value("${rabbitmq.route.dataStore.deleted}")
    private String deletedRouteKey;

    public DataBoxPublisher(RabbitTemplate template) {
        this.template = template;
    }

    public void publishDataBoxUpdatedEvent(DataBox dataBox) {
        log.info("EVENT: Data box updated {}", dataBox);
        template.convertAndSend(dataStoreExchange, updatedRouteKey, dataBox);
    }

    public void publishDataBoxAddedEvent(DataBox dataBox) {
        log.info("EVENT: Data box added {}", dataBox);
        template.convertAndSend(dataStoreExchange, addedRouteKey, dataBox);
    }

    public void publishDataBoxDeletedEvent(DataBox dataBox) {
        log.info("EVENT: Data box deleted {}", dataBox);
        template.convertAndSend(dataStoreExchange, deletedRouteKey, dataBox);
    }
}
