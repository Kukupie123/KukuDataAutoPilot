package dev.kukukodes.kdap.authenticationservice.helpers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
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

    public String convertObjectsToJSON(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();

        return objectMapper.writeValueAsString(object);
    }
}
