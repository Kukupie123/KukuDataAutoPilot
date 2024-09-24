package dev.kukukodes.kdap.authenticationservice.models.userModels;

import dev.kukukodes.kdap.authenticationservice.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

/**
 * Granted authority for KDAP User
 */
@AllArgsConstructor
@ToString
public class KDAPUserAuthority implements GrantedAuthority {
    private final UserRole userRole;
    @Getter
    private final String userID;


    @Override
    public String getAuthority() {
        return userRole.toString();
    }
}
