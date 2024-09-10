package dev.kukukodes.KDAP.Auth.Service.auth.controller;

import dev.kukukodes.KDAP.Auth.Service.auth.service.AuthenticationService;
import dev.kukukodes.KDAP.Auth.Service.auth.dto.IdPasswordAuthenticationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
public class AuthController {

    @Autowired
    AuthenticationService authenticationService;

    @PostMapping({"/authenticate/login", "/api/authenticate/login"})
    Mono<ResponseEntity<String>> idPasswordAuthentication(@RequestBody IdPasswordAuthenticationDTO payload) {
        var auth = authenticationService.authenticateUserAndPassword(payload.id, payload.password);
        return auth.map(authenticated -> authenticated ? ResponseEntity.ok("Authenticated Expect a token soon") : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Credentials"));
    }

}
