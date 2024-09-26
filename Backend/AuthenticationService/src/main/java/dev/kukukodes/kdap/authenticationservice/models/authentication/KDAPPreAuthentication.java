package dev.kukukodes.kdap.authenticationservice.models.authentication;

import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

/**
 * Holds source and id (if source is client)
 */
@Getter
@ToString
public class KDAPPreAuthentication implements Authentication {
    private final String source;
    private final String id;

    public KDAPPreAuthentication(String source, String id) {
        this.source = source;
        this.id = id;
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
        return false;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

    }

    @Override
    public String getName() {
        return "";
    }
}
