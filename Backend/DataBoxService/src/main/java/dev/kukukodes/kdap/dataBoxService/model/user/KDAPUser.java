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
    private LocalDate created;
    private String picture;
    private KDAPUserAuthority authority;
}
