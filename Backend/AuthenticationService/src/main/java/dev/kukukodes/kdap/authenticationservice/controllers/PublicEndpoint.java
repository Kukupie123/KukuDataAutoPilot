package dev.kukukodes.kdap.authenticationservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.kukukodes.kdap.authenticationservice.entity.UserEntity;
import dev.kukukodes.kdap.authenticationservice.models.OAuth2UserInfoGoogle;
import dev.kukukodes.kdap.authenticationservice.models.eventTypes.UserEntityUpdated;
import dev.kukukodes.kdap.authenticationservice.service.JwtService;
import dev.kukukodes.kdap.authenticationservice.service.oAuth.GoogleAuthService;
import dev.kukukodes.kdap.authenticationservice.service.userService.impl.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/public")
public class PublicEndpoint {

    final
    GoogleAuthService googleAuthService;
    private final UserService userService;
    private final JwtService jwtService;
    private final ApplicationEventPublisher eventPublisher;

    public PublicEndpoint(@Autowired GoogleAuthService googleAuthService, UserService userService, JwtService jwtService, ApplicationEventPublisher eventPublisher) {
        this.googleAuthService = googleAuthService;
        this.userService = userService;
        this.jwtService = jwtService;
        this.eventPublisher = eventPublisher;
    }

    /**
     * @return URI to login to google.
     */
    @GetMapping("/login/google")
    public Mono<ResponseEntity<String>> getGoogleLoginURL() {
        log.info("Getting URL Login");
        /*
        Client repository holds all the registered OAuth clients.
        We get google from it and use its data to create a AuthorizationRequest object.
        We then get the requestURL from this AuthReq object and return it as response
         */

        return googleAuthService.createOAuth2AuthReq()
                .map(oAuth2AuthorizationRequest -> ResponseEntity.ok(oAuth2AuthorizationRequest.getAuthorizationRequestUri()))
                .defaultIfEmpty(ResponseEntity.status(404).body("Google Provider not registered."));
    }

    /**
     * Redirect URL that needs to be hit by the provider.
     * This functions Authorizes the user, store the user info in database and generates JWT Token
     *
     * @return JWT Token whose subject is userID, and user info as claims.
     */
    @GetMapping("/redirect/google")
    public Mono<ResponseEntity<String>> handleGoogleRedirect(ServerWebExchange exchange) {
        String code = exchange.getRequest().getQueryParams().getFirst("code");
        String state = exchange.getRequest().getQueryParams().getFirst("state");

        //Get token response
        return googleAuthService.getTokenResponse(code, state)
                //get user from token response
                .flatMap(oAuth2AccessTokenResponse -> googleAuthService.getUserFromToken(oAuth2AccessTokenResponse.getAccessToken()))
                // convert it into user info
                .map(OAuth2UserInfoGoogle::parse)
                // check if the user is in db already. We use optional to define a 'null' value. No nulls allowed in reactive, optional allows us to know if its empty
                .zipWhen(oAuth2UserInfoGoogle -> userService.getUserById(oAuth2UserInfoGoogle.getSub())
                        .map(Optional::of)
                        //If no user was found we use empty optional to signify 'null'
                        .defaultIfEmpty(Optional.empty()))
                // Add update the user to database
                .flatMap(objects -> {
                    var auth = objects.getT1();
                    if (objects.getT2().isEmpty()) {
                        log.info("Found no existing user. Creating a new record");
                        return userService.addUser(UserEntity.createUserFromOAuthUserInfoGoogle(objects.getT1()));
                    }
                    log.info("Found existing user.");
                    UserEntity user = objects.getT2().get();
                    if (user.getId().equals(auth.getSub()) &&
                            user.getName().equals(auth.getName()) &&
                            user.getEmail().equals(auth.getEmailID()) &&
                            user.getPicture().equals(auth.getPictureURL())
                    ) {
                        log.info("All fields are still the same. Skipping update");
                        return Mono.just(user);
                    }
                    log.info("Fields are outdated. Updating user");
                    //TODO: When fields are updated fire a broadcast in service level using message brokers.
                    var updatedUser = UserEntity.updateUserFromOAuthUserInfoGoogle(auth, user);
                    //Fire event that user entity has been updated.
                    eventPublisher.publishEvent(new UserEntityUpdated(updatedUser));
                    return userService.updateUser(updatedUser);
                })
                //Generate token based on user updated/added
                .map(userEntity -> ResponseEntity.ok(jwtService.generateJwtToken(userEntity.generateClaimsForJwtToken())))
                .onErrorResume(throwable -> Mono.just(ResponseEntity.internalServerError().body(throwable.getMessage() + "\n" + Arrays.toString(throwable.getStackTrace()))))
                ;
    }

    /**
     * Extracts jwt token from Authorization header and uses it to extract claims
     *
     * @return Map of claims as string, optionally may return new a boolean field to know that it's time to generate a new token since this one contains outdated claims.
     */
    @GetMapping("/validate")
    public Mono<ResponseEntity<String>> validateToken(ServerWebExchange exchange) {
        String headerAuth = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (headerAuth == null || !headerAuth.startsWith("Bearer ")) {
            return Mono.just(ResponseEntity.badRequest().body("Invalid/Missing Authorization Token. It needs to be in the form 'Bearer .........'"));
        }
        try {
            //TODO: If user is outdated make sure to get latest information about user and send optional attribute to let client know that its token has outdated claims
            String token = headerAuth.substring(7);
            var claims = jwtService.extractClaimsFromJwtToken(token);
            Map<String, String> claimsMap = new HashMap<>();
            claims.forEach((claim, value) -> claimsMap.put(claim, String.valueOf(value)));
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(claimsMap);
            return Mono.just(ResponseEntity.ok(jsonString));

        } catch (Exception e) {
            log.error(e.getMessage());
            log.error(Arrays.toString(e.getStackTrace()));
            return Mono.just(ResponseEntity.internalServerError().body(e.getMessage()));
        }

    }


}
