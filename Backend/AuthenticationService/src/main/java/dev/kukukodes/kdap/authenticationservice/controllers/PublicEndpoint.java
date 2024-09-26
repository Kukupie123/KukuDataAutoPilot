package dev.kukukodes.kdap.authenticationservice.controllers;

import dev.kukukodes.kdap.authenticationservice.constants.AccessLevelConst;
import dev.kukukodes.kdap.authenticationservice.entity.user.KDAPUserEntity;
import dev.kukukodes.kdap.authenticationservice.helpers.SecurityHelper;
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
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/public")
public class PublicEndpoint {

    final GoogleAuthService googleAuthService;
    private final UserService userService;
    private final JwtService jwtService;
    private final SecurityHelper securityHelper;

    public PublicEndpoint(GoogleAuthService googleAuthService, @Qualifier(AccessLevelConst.ADMIN) UserService userService, JwtService jwtService, ApplicationEventPublisher eventPublisher, UserEventPublisher userEventPublisher, SecurityHelper securityHelper) {
        this.googleAuthService = googleAuthService;
        this.userService = userService;
        this.jwtService = jwtService;
        this.securityHelper = securityHelper;
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
    public Mono<ResponseEntity<ResponseModel<String>>> handleGoogleRedirect(ServerWebExchange exchange) {
        String code = exchange.getRequest().getQueryParams().getFirst("code");
        String state = exchange.getRequest().getQueryParams().getFirst("state");
        log.info("redirected to google with code {}", code);
        if (code == null) {
            return Mono.just(ResponseModel.buildResponse("Code missing from query param", null, 405));
        }
        //Get token response
        return googleAuthService.getTokenResponse(code, state)
                //get user from token response
                .flatMap(oAuth2AccessTokenResponse -> googleAuthService.getUserFromToken(oAuth2AccessTokenResponse.getAccessToken()))
                // convert it into user info
                .flatMap(oAuth2User -> {
                    //Check if the user is already stored in database
                    var authUser = OAuth2UserInfoGoogle.fromOAuth2User(oAuth2User);
                    return userService.getUserById(authUser.getSub())
                            .map(Optional::of)
                            .defaultIfEmpty(Optional.empty())
                            .flatMap(userEntity -> {
                                if (userEntity.isEmpty()) {
                                    log.warn("No user found");
                                    return userService.addUser(userEntity.get());
                                }
                                log.info("Found existing user");
                                //get new user with updated fields
                                KDAPUserEntity updatedUser = userEntity.get().updatePropertiesFromOAuth2UserInfoGoogle(authUser, securityHelper);
                                //This function returns empty if user doesn't need to be updated so we send back default user if empty
                                return userService.updateUser(updatedUser)
                                        .defaultIfEmpty(updatedUser)
                                        ;
                            })
                            //Generate token based on user updated/added
                            .map(userEntity -> {
                                Claims claims = jwtService.createClaimsForUser(userEntity);
                                String token = jwtService.generateJwtToken(claims);
                                return ResponseModel.success("generated token", token);
                            }).onErrorResume(throwable -> {
                                log.error(throwable.getMessage(), throwable);
                                log.error(Arrays.toString(Arrays.stream(throwable.getStackTrace()).toArray()));
                                return Mono.just(ResponseModel.buildResponse(throwable.getMessage(), null, 500));
                            });
                });
    }
}
