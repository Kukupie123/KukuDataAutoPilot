package dev.kukukodes.kdap.authenticationservice.helpers;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQHelper {

    @Value("${spring.application.name}")
    private String applicationName;


    public String createExchangeName(String exchangeName) {
        return applicationName + "." + exchangeName;
    }

    public String createQueueName(String queueName, String exchangeName) {
        return applicationName + "." + exchangeName + "." + queueName;
    }

    public String createRoutingKey(String queueName, String exchangeName) {
        return applicationName + "." + exchangeName + "." + exchangeName;
    }


}
