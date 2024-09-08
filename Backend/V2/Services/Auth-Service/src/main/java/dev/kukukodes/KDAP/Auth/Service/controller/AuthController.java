package dev.kukukodes.KDAP.Auth.Service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class AuthController {
    @GetMapping("/")
    Mono<ResponseEntity<String>> login() {
        return Mono.just(ResponseEntity.ok("Hello World"));
    }
}
