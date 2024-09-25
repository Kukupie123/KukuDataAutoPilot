package dev.kukukodes.kdap.authenticationservice.models.userModels;

import dev.kukukodes.kdap.authenticationservice.entity.user.KDAPUserEntity;
import dev.kukukodes.kdap.authenticationservice.enums.AuthAccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KDAPUserPayload {

    String id;
    String name;
    LocalDate created;
    String email;
    String picture;
    AuthAccessLevel authority;

    public static KDAPUserPayload fromKDAPUserEntity(KDAPUserEntity kdapUserEntity, AuthAccessLevel role) {
        return new KDAPUserPayload(
                kdapUserEntity.getId(),
                kdapUserEntity.getName(),
                kdapUserEntity.getCreated(),
                kdapUserEntity.getEmail(),
                kdapUserEntity.getPicture(),
                role
        );
    }
}
