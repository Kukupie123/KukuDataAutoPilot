package dev.kukukodes.kdap.authenticationservice.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class JsonHelper {
    private final ObjectMapper objectMapper;

    public JsonHelper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String convertObjectsToJSON(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    public <T> T convertJsonToObj(String json, Class<? extends T> type) throws JsonProcessingException {
        return objectMapper.readValue(json, type);
    }
}
