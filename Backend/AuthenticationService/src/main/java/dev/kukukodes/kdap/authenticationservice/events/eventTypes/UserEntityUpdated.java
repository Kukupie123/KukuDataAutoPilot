package dev.kukukodes.kdap.authenticationservice.events.eventTypes;

import dev.kukukodes.kdap.authenticationservice.entity.UserEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserEntityUpdated extends ApplicationEvent {
    private final UserEntity updatedUserEntity;

    public UserEntityUpdated(Object source, UserEntity updatedUserEntity) {
        super(source);
        this.updatedUserEntity = updatedUserEntity;
    }
}
