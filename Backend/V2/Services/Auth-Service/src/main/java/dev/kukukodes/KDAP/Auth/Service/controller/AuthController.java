package dev.kukukodes.KDAP.Auth.Service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class AuthController {
    /**
     * Authenticate through ID&Password or OAuth
     * @return
     */
    @GetMapping("/authenticate")
    Mono<ResponseEntity<String>> login() {
        //HOw to show the default login page?
        return Mono.just(ResponseEntity.ok("Hello World"));
    }
}
