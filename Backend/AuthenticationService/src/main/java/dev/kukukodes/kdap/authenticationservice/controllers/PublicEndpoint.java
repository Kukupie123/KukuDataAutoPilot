package dev.kukukodes.kdap.authenticationservice.controllers;

import dev.kukukodes.kdap.authenticationservice.constants.AccessLevelConst;
import dev.kukukodes.kdap.authenticationservice.entity.user.KDAPUserEntity;
import dev.kukukodes.kdap.authenticationservice.models.ResponseModel;
import dev.kukukodes.kdap.authenticationservice.models.userModels.OAuth2UserInfoGoogle;
import dev.kukukodes.kdap.authenticationservice.publishers.UserEventPublisher;
import dev.kukukodes.kdap.authenticationservice.service.JwtService;
import dev.kukukodes.kdap.authenticationservice.service.UserService;
import dev.kukukodes.kdap.authenticationservice.service.oAuth.GoogleAuthService;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
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

    final GoogleAuthService googleAuthService;
    private final UserService userService;
    private final JwtService jwtService;

    public PublicEndpoint(GoogleAuthService googleAuthService, @Qualifier(AccessLevelConst.ADMIN) UserService userService, JwtService jwtService, ApplicationEventPublisher eventPublisher, UserEventPublisher userEventPublisher) {
        this.googleAuthService = googleAuthService;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    /**
     * @return URI to login to google.
     */
    @GetMapping("/login/google")
    public Mono<ResponseEntity<ResponseModel<String>>> getGoogleLoginURL() {
        log.info("Getting URL Login");
        return googleAuthService
                .createOAuth2AuthReq()
                .map(oAuth2AuthorizationRequest -> ResponseModel.success("generated login url", oAuth2AuthorizationRequest.getAuthorizationRequestUri()))
                .defaultIfEmpty(ResponseModel.buildResponse("Google Provider not registered.", null, 404));
    }

    /**
     * Redirect URL that needs to be hit by the provider.
     * This functions Authorizes the user, store the user info in database and generates JWT Token
     * <p>
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
                    //Check if the user is already stored in database
                    var authUser = OAuth2UserInfoGoogle.fromOAuth2User(oAuth2User);
                    return userService.getUserById(authUser.getSub())
                            .flatMap(userEntity -> {
                                log.info("Found existing user");
                                //get new user with updated fields
                                KDAPUserEntity updatedUser = userEntity.updatePropertiesFromOAuth2UserInfoGoogle(authUser);
                                return userService.updateUser(updatedUser);
                            })
                            //Generate token based on user updated/added
                            .map(userEntity -> {
                                Claims claims = jwtService.createClaimsForUser(userEntity);
                                String token = jwtService.generateJwtToken(claims);
                                return ResponseEntity.ok(token);
                            }).onErrorResume(throwable -> Mono.just(ResponseEntity.internalServerError().body(throwable.getMessage() + "\n" + Arrays.toString(throwable.getStackTrace()))));
                });
    }
}
