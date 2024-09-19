package dev.kukukodes.kdap.authenticationservice.events.listeners;

import dev.kukukodes.kdap.authenticationservice.events.eventTypes.UserEntityUpdated;
import dev.kukukodes.kdap.authenticationservice.service.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserEntityUpdatedListener {

    @Autowired
    JwtService jwtService;

    @EventListener
    public void updateClaims(UserEntityUpdated event) {
        log.info("User has been updated. Updating JWTService's userEntity from source : {} -> {}", event.getSource(), event.getUpdatedUserEntity());
        jwtService.updatedUserEntity = event.getUpdatedUserEntity();
    }
}
