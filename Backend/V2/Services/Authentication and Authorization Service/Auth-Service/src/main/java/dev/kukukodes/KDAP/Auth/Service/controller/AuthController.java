package dev.kukukodes.KDAP.Auth.Service.controller;

import dev.kukukodes.KDAP.Auth.Service.repo.IUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
public class AuthController {
    @Autowired
    IUserRepository userRepository;

    /**
     * Authenticate through ID&Password or OAuth
     *
     * @return
     */
    @GetMapping("/authenticate")
    Mono<ResponseEntity<String>> login() {
        //HOw to show the default login page?
        return Mono.just(ResponseEntity.ok("Hello World"));
    }

    @GetMapping("/get")
    Mono<ResponseEntity<String>> get() {
        return userRepository.getAllUsers().collectList().flatMap(userDbLevels -> Mono.just(ResponseEntity.ok(userDbLevels.toString())));
    }

}
