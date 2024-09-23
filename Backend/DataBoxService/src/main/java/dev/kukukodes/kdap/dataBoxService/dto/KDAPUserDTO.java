package dev.kukukodes.kdap.dataBoxService.dto;

import dev.kukukodes.kdap.dataBoxService.enums.KDAPUserAuthority;
import dev.kukukodes.kdap.dataBoxService.model.KDAPAuthenticatedUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class KDAPUserDTO {
    private String id;
    private String email;
    private String name;
    private LocalDate created;
    private LocalDate updated;
    private String picture;
    private KDAPUserAuthority authority; //ADMIN, USER

    public KDAPAuthenticatedUser getKDAPUser(){
        return new KDAPAuthenticatedUser(this);
    }
}
