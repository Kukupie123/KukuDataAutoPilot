package dev.kukukodes.kdap.authenticationservice.controllers;

import dev.kukukodes.kdap.authenticationservice.dto.user.UserRequestDTO;
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

    public AuthenticatedEndpoint(UserService userService, RequestHelper requestHelper, JwtService jwtService) {
        this.userService = userService;
        this.requestHelper = requestHelper;
        this.jwtService = jwtService;
    }

    /**
     * Get user data based on token
     */
    @GetMapping("/")
    public Mono<ResponseEntity<UserRequestDTO>> index(ServerWebExchange exchange) {
        String token = requestHelper.extractToken(exchange.getRequest().getHeaders().getFirst("Authorization"));
        Claims claims = jwtService.extractClaimsFromJwtToken(token);
        String userID = claims.getSubject();
        return userService.getUserById(userID)
                .map(UserRequestDTO::fromUserEntity)
                .flatMap(userRequestDTO -> Mono.just(ResponseEntity.ok(userRequestDTO)))
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
