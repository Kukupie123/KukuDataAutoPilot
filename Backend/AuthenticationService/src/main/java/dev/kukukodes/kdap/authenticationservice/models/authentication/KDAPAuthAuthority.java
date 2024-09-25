package dev.kukukodes.kdap.authenticationservice.models.authentication;

import dev.kukukodes.kdap.authenticationservice.enums.AuthAccessLevel;
import org.springframework.security.core.GrantedAuthority;

public class KDAPAuthAuthority implements GrantedAuthority {
    private final AuthAccessLevel accessLevel;

    public KDAPAuthAuthority(AuthAccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }

    @Override
    public String getAuthority() {
        return accessLevel.name();
    }
}
