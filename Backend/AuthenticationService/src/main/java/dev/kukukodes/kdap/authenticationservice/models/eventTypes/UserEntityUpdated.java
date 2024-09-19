package dev.kukukodes.kdap.authenticationservice.models.eventTypes;

import dev.kukukodes.kdap.authenticationservice.entity.UserEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserEntityUpdated extends ApplicationEvent {
    private final UserEntity updatedUserEntity;
    public UserEntityUpdated(UserEntity updatedUserEntity) {
        super(updatedUserEntity);
        this.updatedUserEntity = updatedUserEntity;
    }
}
