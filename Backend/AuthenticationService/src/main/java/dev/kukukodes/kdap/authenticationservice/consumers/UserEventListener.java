package dev.kukukodes.kdap.authenticationservice.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.kukukodes.kdap.authenticationservice.constants.RabbitMQConst;
import dev.kukukodes.kdap.authenticationservice.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumes user events
 */
@Slf4j
@Component
public class UserEventListener {

    private final ObjectMapper objectMapper;

    public UserEventListener(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Listens for user update events on a RabbitMQ queue.
     * <p>
     * The @RabbitListener annotation uses a Spring Expression Language (SpEL) expression
     * to dynamically resolve the queue name:
     * - #{...} denotes a SpEL expression
     * - @rabbitMQConst refers to the RabbitMQConst bean in the Spring context
     * - .queues accesses the Queues inner class of RabbitMQConst
     * - .getUserUpdatedQueue() calls the method to get the dynamic queue name
     * <p>
     * This approach allows for runtime resolution of the queue name, enabling
     * dynamic queue naming strategies while still satisfying the @RabbitListener's
     * requirement for a constant expression.
     *
     * @param updatedUserJSON The JSON string representation of the updated user
     * @throws JsonProcessingException if there's an error parsing the JSON
     */
    @RabbitListener(queues = "#{@rabbitMQConst.queues.userUpdatedQueue}")
    public void onUserUpdated(String updatedUserJSON) throws JsonProcessingException {
        var user = objectMapper.readValue(updatedUserJSON, UserEntity.class);
        log.info("Updated user : {}", user);
    }

}
