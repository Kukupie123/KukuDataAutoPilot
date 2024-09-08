package dev.kukukodes.KDAP.Auth.Service.security;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CustomAuthenticationConverter implements ServerAuthenticationConverter {
    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return exchange.getRequest().getBody().next().flatMap(dataBuffer -> {
            // Convert DataBuffer to String
            String body = dataBuffer.toString(StandardCharsets.UTF_8);

            // Parse the JSON to extract username and password
            // Assuming the body is in JSON format: {"username": "user", "password": "pass"}
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(body);
                String username = jsonNode.get("username").asText();
                String password = jsonNode.get("password").asText();

                // Create an Authentication object
                return Mono.just(new UsernamePasswordAuthenticationToken(username, password));
            } catch (IOException e) {
                return Mono.empty();
            }
        });
    }

}
