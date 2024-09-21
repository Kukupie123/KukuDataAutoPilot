package dev.kukukodes.kdap.authenticationservice.dto.user;

import dev.kukukodes.kdap.authenticationservice.entity.UserEntity;
import lombok.*;

import java.time.DateTimeException;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserRequestDTO {
    private String email;
    private String name;
    private LocalDate created;
    private LocalDate updated;
    private String picture;

    public static UserRequestDTO fromUserEntity(UserEntity userEntity) {
        if (userEntity.getCreated().isAfter(userEntity.getUpdated())) {
            throw new DateTimeException("Created date is greater than updated date");
        }
        return new UserRequestDTO(userEntity.getEmail(), userEntity.getName(), userEntity.getCreated(), userEntity.getUpdated(), userEntity.getPicture());
    }
}
