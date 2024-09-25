package dev.kukukodes.kdap.authenticationservice.models.authentication;

import dev.kukukodes.kdap.authenticationservice.entity.user.KDAPUserEntity;
import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

/**
 * Represents authenticated request
 */
@Getter
public class KDAPAuthenticated implements Authentication {
    private final String accessLevel;
    private final String source;
    private final KDAPUserEntity user;

    public KDAPAuthenticated(String accessLevel, String source, KDAPUserEntity user) {
        this.accessLevel = accessLevel;
        this.source = source;
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

    }

    @Override
    public String getName() {
        return "";
    }
}
