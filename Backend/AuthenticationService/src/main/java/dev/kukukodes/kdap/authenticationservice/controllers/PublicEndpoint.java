package dev.kukukodes.kdap.authenticationservice.controllers;

import dev.kukukodes.kdap.authenticationservice.entity.UserEntity;
import dev.kukukodes.kdap.authenticationservice.models.OAuth2UserInfoGoogle;
import dev.kukukodes.kdap.authenticationservice.service.JwtService;
import dev.kukukodes.kdap.authenticationservice.service.oAuth.GoogleAuthService;
import dev.kukukodes.kdap.authenticationservice.service.userService.impl.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/api/public")
public class PublicEndpoint {

    final
    GoogleAuthService googleAuthService;
    private final UserService userService;
    private final JwtService jwtService;

    public PublicEndpoint(@Autowired GoogleAuthService googleAuthService, UserService userService, JwtService jwtService) {
        this.googleAuthService = googleAuthService;
        this.userService = userService;
        this.jwtService = jwtService;
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
                // check if the user is in db already
                .flatMap(oAuth2UserInfoGoogle -> userService.getUserById(oAuth2UserInfoGoogle.getSub())
                        //If found then update the user
                        .flatMap(userEntity -> {
                            log.info("Existing user found, updating info");
                            userEntity.setActivity(new Date());
                            userEntity.setName(userEntity.getName());
                            userEntity.setPicture(userEntity.getPicture());
                            userEntity.setEmail(userEntity.getEmail());
                            return userService.updateUser(userEntity);
                        })
                        //If not found then create a new record
                        .switchIfEmpty(Mono.defer(() -> {
                            log.info("No existing user found, creating new one");
                            var newUser = new UserEntity(
                                    oAuth2UserInfoGoogle.getSub(),
                                    oAuth2UserInfoGoogle.getName(),
                                    "",
                                    new Date(),
                                    new Date(),
                                    new Date(),
                                    oAuth2UserInfoGoogle.getEmailID(),
                                    oAuth2UserInfoGoogle.getPictureURL()
                            );
                            return userService.addUser(newUser);
                        }))
                )
                //Generate token based on user updated/added
                .map(userEntity -> ResponseEntity.ok(jwtService.generateUserJwtToken(userEntity)))
                .onErrorResume(throwable -> Mono.just(ResponseEntity.internalServerError().body(throwable.getMessage())))
                ;
    }


}
