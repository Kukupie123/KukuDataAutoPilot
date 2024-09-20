package dev.kukukodes.kdap.authenticationservice.controllers;

import dev.kukukodes.kdap.authenticationservice.dto.user.UpdateUserDTO;
import dev.kukukodes.kdap.authenticationservice.helpers.RequestHelper;
import dev.kukukodes.kdap.authenticationservice.service.JwtService;
import dev.kukukodes.kdap.authenticationservice.service.UserService;
import io.jsonwebtoken.Claims;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.swing.text.html.Option;
import java.util.Optional;

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

    @GetMapping("/")
    public Mono<String> index() {
        return Mono.just("Welcome to authenticated section");
    }

    @PutMapping("/")
    public Mono<String> updateUser(@RequestBody UpdateUserDTO payload, ServerWebExchange exchange) {
        String token = requestHelper.extractToken(exchange.getRequest().getHeaders().getFirst("Authorization"));
        Claims claims = jwtService.extractClaimsFromJwtToken(token);
        return userService.getUserById(String.valueOf(claims.getSubject()))
                .map(Optional::of)
                .defaultIfEmpty(Optional.empty())
                .flatMap(userEntity -> {
                    if (userEntity.isEmpty()) {
                        return Mono.just("User not found");
                    }
                    var userDb = userEntity.get();
                    boolean needsUpdate = false;
                    if (payload.getName() != null && !payload.getName().isEmpty() && !userDb.getName().equals(payload.getName())) {
                        needsUpdate = true;
                        userDb.setName(payload.getName());
                    }
                    if (payload.getEmail() != null && !payload.getEmail().isEmpty() && !userDb.getEmail().equals(payload.getEmail())) {
                        needsUpdate = true;
                        userDb.setEmail(payload.getEmail());
                    }

                    if (needsUpdate) {
                        return userService.updateUser(userDb).map(user -> "Updated user");
                    }
                    return Mono.just("User doesn't need to be updated");

                })
                ;
    }
}
