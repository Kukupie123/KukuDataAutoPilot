package dev.kukukodes.KDAP.Auth.controllers;

import dev.kukukodes.KDAP.Auth.services.auth.impl.AuthenticationService;
import dev.kukukodes.KDAP.Auth.dataTransferObject.IdPasswordAuthenticationDTO;
import dev.kukukodes.KDAP.Auth.repo.database.IUserRepository;
import dev.kukukodes.KDAP.Auth.services.jwt.JWTService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;

@Slf4j
@RestController
public class AuthController {

    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    IUserRepository userRepository;
    @Autowired
    JWTService jwtService;

    @PostMapping({"/authenticate/login", "/api/authenticate/login"})
    Mono<ResponseEntity<String>> idPasswordAuthentication(@RequestBody IdPasswordAuthenticationDTO payload) {
        var auth = authenticationService.authenticateUserAndPassword(payload.getId(), payload.getPassword());

        return auth.map(authenticated -> {
            if (authenticated) {
                log.info("User {} authenticated successfully", payload.getId());
                HashMap<String, Object> claimsMap = new HashMap<>();
                log.info("Generating JWT Token with claims {}", claimsMap);
                String generatedToken = jwtService.generateJWTToken(claimsMap, payload.id);
                log.info("Generated JWT Token: ...{}...", generatedToken.substring(4, 8));
                return ResponseEntity.ok()
                        .header("Authorization", "Bearer " + generatedToken)
                        .body("Login Successful with JWT Token");
            } else {
                log.warn("Failed login attempt for user {}", payload.getId());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Credentials");
            }
        });
    }

    @GetMapping("/")
    public Flux<ResponseEntity<String>> getUsers() {
        return userRepository.getAllUsers().map( userDbLevel -> ResponseEntity.ok().body(userDbLevel.toString()));
    }
}
