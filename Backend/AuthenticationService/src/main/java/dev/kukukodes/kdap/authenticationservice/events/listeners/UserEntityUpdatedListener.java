package dev.kukukodes.kdap.authenticationservice.events.listeners;

import dev.kukukodes.kdap.authenticationservice.events.eventTypes.UserEntityUpdated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserEntityUpdatedListener {

    @EventListener
    public void updateClaims(UserEntityUpdated event) {
        log.info("User has been updated. Updating JWTService's userEntity from source : {} -> {}", event.getSource(), event.getUpdatedUserEntity());
    }
}
