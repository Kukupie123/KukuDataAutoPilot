package dev.kukukodes.kdap.authenticationservice.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.kukukodes.kdap.authenticationservice.dto.user.UserRequestDTO;
import dev.kukukodes.kdap.authenticationservice.helpers.JsonHelper;
import dev.kukukodes.kdap.authenticationservice.helpers.RequestHelper;
import dev.kukukodes.kdap.authenticationservice.service.JwtService;
import dev.kukukodes.kdap.authenticationservice.service.UserService;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/authenticated")
public class AuthenticatedEndpoint {

    private final UserService userService;
    private final RequestHelper requestHelper;
    private final JwtService jwtService;
    private final JsonHelper jsonHelper;

    public AuthenticatedEndpoint(UserService userService, RequestHelper requestHelper, JwtService jwtService, JsonHelper jsonHelper) {
        this.userService = userService;
        this.requestHelper = requestHelper;
        this.jwtService = jwtService;
        this.jsonHelper = jsonHelper;
    }

    /**
     * Get user id based on token
     */
    @GetMapping("/")
    public Mono<ResponseEntity<String>> index(ServerWebExchange exchange) {
        String token = requestHelper.extractToken(exchange.getRequest().getHeaders().getFirst("Authorization"));
        Claims claims = jwtService.extractClaimsFromJwtToken(token);
        String userID = claims.getSubject();
        return userService.getUserById(userID)
                .map(UserRequestDTO::fromUserEntity)
                .flatMap(userRequestDTO -> {
                    try {
                        return Mono.just(ResponseEntity.ok(jsonHelper.convertObjectsToJSON(userRequestDTO)));
                    } catch (JsonProcessingException e) {
                        return Mono.error(e);
                    }
                })
                .onErrorResume(throwable -> Mono.just(ResponseEntity.internalServerError().body(throwable.getMessage())))
                ;
    }

    @PutMapping("/")
    public Mono<ResponseEntity<String>> updateUser(@RequestBody UserRequestDTO payload, ServerWebExchange exchange) {
        String token = requestHelper.extractToken(exchange.getRequest().getHeaders().getFirst("Authorization"));
        Claims claims = jwtService.extractClaimsFromJwtToken(token);
        String userID = claims.getSubject();
        return userService.getUserById(userID)
                .flatMap(userEntity -> {
                    boolean needsUpdate = false;
                    if (payload.getName() != null && !payload.getName().isEmpty() && !userEntity.getName().equals(payload.getName())) {
                        needsUpdate = true;
                        userEntity.setName(payload.getName());
                    }
                    if (payload.getEmail() != null && !payload.getEmail().isEmpty() && !userEntity.getEmail().equals(payload.getEmail())) {
                        needsUpdate = true;
                        userEntity.setEmail(payload.getEmail());
                    }

                    if (needsUpdate) {
                        return userService.updateUser(userEntity).map(user -> "Updated user")
                                .map(ResponseEntity::ok);

                    }
                    return Mono.just(ResponseEntity.ok("User doesn't need to be updated"));
                })
                .defaultIfEmpty(ResponseEntity.notFound().build())
                ;
    }
}
