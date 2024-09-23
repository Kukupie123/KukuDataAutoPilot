package dev.kukukodes.kdap.dataBoxService.dto;

import dev.kukukodes.kdap.dataBoxService.model.KDAPUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class UserDataDTO {
    private String id;
    private String email;
    private String name;
    private LocalDate created;
    private LocalDate updated;
    private String picture;

    public KDAPUser getKDAPUser(){
        return new KDAPUser(this);
    }
}
