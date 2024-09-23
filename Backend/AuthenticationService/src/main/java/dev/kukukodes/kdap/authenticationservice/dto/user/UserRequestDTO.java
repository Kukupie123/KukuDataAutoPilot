package dev.kukukodes.kdap.authenticationservice.dto.user;

import dev.kukukodes.kdap.authenticationservice.entity.UserEntity;
import dev.kukukodes.kdap.authenticationservice.enums.UserRole;
import dev.kukukodes.kdap.authenticationservice.models.KDAPUserAuthority;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;

import java.time.DateTimeException;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserRequestDTO {
    private String id;
    private String email;
    private String name;
    private String password;
    private LocalDate created;
    private LocalDate updated;
    private String picture;
    private UserRole authority;


    public static UserRequestDTO fromUserEntity(UserEntity userEntity, boolean includePassword, @Value("${superemail}") String superEmail) {
        if (userEntity.getCreated().isAfter(userEntity.getUpdated())) {
            throw new DateTimeException("Created date is greater than updated date");
        }
        return new UserRequestDTO(userEntity.getId(),
                userEntity.getEmail(),
                userEntity.getName(),
                includePassword ? userEntity.getPassword() : "",
                userEntity.getCreated(),
                userEntity.getUpdated(),
                userEntity.getPicture(),
                superEmail.equals(UserRole.ADMIN.toString()) ? UserRole.ADMIN : UserRole.USER
        );

    }
}
