package dev.kukukodes.kdap.authenticationservice.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.kukukodes.kdap.authenticationservice.entity.UserEntity;
import dev.kukukodes.kdap.authenticationservice.models.OAuth2UserInfoGoogle;
import dev.kukukodes.kdap.authenticationservice.service.JwtService;
import dev.kukukodes.kdap.authenticationservice.publishers.UserEventPublisher;
import dev.kukukodes.kdap.authenticationservice.service.oAuth.GoogleAuthService;
import dev.kukukodes.kdap.authenticationservice.service.UserService;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Slf4j
@RestController
@RequestMapping("/api/public")
public class PublicEndpoint {

    final
    GoogleAuthService googleAuthService;
    private final UserService userService;
    private final JwtService jwtService;
    private final UserEventPublisher userEventPublisher;

    public PublicEndpoint(GoogleAuthService googleAuthService, UserService userService, JwtService jwtService, ApplicationEventPublisher eventPublisher, UserEventPublisher userEventPublisher) {
        this.googleAuthService = googleAuthService;
        this.userService = userService;
        this.jwtService = jwtService;
        this.userEventPublisher = userEventPublisher;
    }

    /**
     * @return URI to login to google.
     */
    @GetMapping("/login/google")
    public Mono<ResponseEntity<String>> getGoogleLoginURL() {
        log.info("Getting URL Login");
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
                .flatMap(oAuth2User -> {
                    var authUser = OAuth2UserInfoGoogle.fromOAuth2User(oAuth2User);
                    //Check if the user is already stored in database
                    return userService
                            .getUserById(authUser.getSub())
                            //User not found in database
                            .switchIfEmpty(Mono.defer(() -> {
                                log.info("User not found in database. Adding user to database");
                                return userService.addUser(UserEntity.createUserFromOAuthUserInfoGoogle(authUser));
                            }))
                            .flatMap(userEntity -> {
                                log.info("Found existing user");
                                UserEntity updatedUser = userService.updateUserFromOAuthUserInfoGoogle(authUser, userEntity);
                                return userService.updateUser(updatedUser)
                                        //Publish event on updating user.
                                        .doOnSuccess(updatedUserDB -> {
                                            try {
                                                userEventPublisher.publishUserUpdateMsg(updatedUserDB); //Publish user updated message
                                            } catch (JsonProcessingException e) {
                                                throw new RuntimeException(e);
                                            }
                                        });
                            })
                            //Generate token based on user updated/added
                            .map(userEntity -> {
                                Claims claims = jwtService.createClaimsForUser(userEntity);
                                String token = jwtService.generateJwtToken(claims);
                                return ResponseEntity.ok(token);
                            })
                            .onErrorResume(throwable -> Mono.just(ResponseEntity.internalServerError().body(throwable.getMessage() + "\n" + Arrays.toString(throwable.getStackTrace()))))
                            ;
                });
    }
}
