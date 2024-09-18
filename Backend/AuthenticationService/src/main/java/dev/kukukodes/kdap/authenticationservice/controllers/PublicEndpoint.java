package dev.kukukodes.kdap.authenticationservice.controllers;

import dev.kukukodes.kdap.authenticationservice.entity.UserEntity;
import dev.kukukodes.kdap.authenticationservice.models.OAuth2UserInfoGoogle;
import dev.kukukodes.kdap.authenticationservice.service.JwtService;
import dev.kukukodes.kdap.authenticationservice.service.oAuth.GoogleAuthService;
import dev.kukukodes.kdap.authenticationservice.service.userService.impl.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.user.OAuth2User;
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

        //1. Get access token
        Mono<OAuth2AccessTokenResponse> accessTokenResponseMono = googleAuthService.getTokenResponse(code, state);
        //2. Use access token to get OAuth2 User
        Mono<OAuth2User> oAuth2UserMono = accessTokenResponseMono.flatMap(oAuth2AccessTokenResponse -> googleAuthService.getUserFromToken(oAuth2AccessTokenResponse.getAccessToken()));
        //3. Parse OAuth2UserMono into OAuth2UserInfoGoogle by using OAuth2userInfoGoogle.parse()
        Mono<OAuth2UserInfoGoogle> userInfoGoogleMono = oAuth2UserMono.map(OAuth2UserInfoGoogle::parse);
        //4. Attempt to get the user using sub as its ID. If none found we create a new one
        Mono<UserEntity> foundUser = userInfoGoogleMono.flatMap(oAuth2UserInfoGoogle -> userService
                .getUserById(oAuth2UserInfoGoogle.getSub())
                .defaultIfEmpty(new UserEntity(
                        oAuth2UserInfoGoogle.getSub(),
                        oAuth2UserInfoGoogle.getName(),
                        "",
                        new Date(),
                        new Date(),
                        new Date(),
                        oAuth2UserInfoGoogle.getEmailID(),
                        oAuth2UserInfoGoogle.getPictureURL()
                )));
        //5. Add the updated userEntity to database
        var addedUser = foundUser.flatMap(userService::AddUpdateUser);
        //6. Generate JWT token based on it and return it
        return addedUser.doOnError(throwable -> ResponseEntity.internalServerError().body(throwable.getMessage()))
                .map(userEntity -> ResponseEntity.ok(jwtService.generateUserJwtToken(userEntity)));

    }


}
