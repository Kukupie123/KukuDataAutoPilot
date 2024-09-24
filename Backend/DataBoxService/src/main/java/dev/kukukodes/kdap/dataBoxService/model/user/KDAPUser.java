package dev.kukukodes.kdap.dataBoxService.model.user;

import dev.kukukodes.kdap.dataBoxService.enums.KDAPUserAuthority;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KDAPUser {
    private String id;
    private String email;
    private String name;
    private String password;
    private LocalDate created;
    private LocalDate updated;
    private String picture;
    private KDAPUserAuthority authority;
    public KDAPAuthenticatedUser getKDAPUser(){
        return new KDAPAuthenticatedUser(this);
    }
}
