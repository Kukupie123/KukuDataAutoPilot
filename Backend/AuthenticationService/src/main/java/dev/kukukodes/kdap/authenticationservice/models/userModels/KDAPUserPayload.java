package dev.kukukodes.kdap.authenticationservice.models.userModels;

import dev.kukukodes.kdap.authenticationservice.entity.user.KDAPUserEntity;
import dev.kukukodes.kdap.authenticationservice.enums.UserRole;
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
    UserRole authority;

    public static KDAPUserPayload fromKDAPUserEntity(KDAPUserEntity kdapUserEntity, UserRole role) {
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
