package dev.kukukodes.kdap.authenticationservice.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/authenticated")
public class AuthenticatedEndpoint {
    @GetMapping("/")
    public Mono<String> index() {
        return Mono.just("Welcome to authenticated section");
    }
}
