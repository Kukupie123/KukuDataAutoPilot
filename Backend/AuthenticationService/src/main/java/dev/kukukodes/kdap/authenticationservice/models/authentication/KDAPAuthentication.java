package dev.kukukodes.kdap.authenticationservice.models.authentication;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

public class KDAPAuthentication implements Authentication {
    private final KDAPAuthAuthority authority;
    private final boolean isAuthenticated;
    public KDAPAuthentication(KDAPAuthAuthority authority, boolean isAuthenticated) {
        this.authority = authority;
        this.isAuthenticated = isAuthenticated;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(authority);
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
        return isAuthenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        throw new IllegalArgumentException("Can't change authentication");
    }

    @Override
    public String getName() {
        return "KDAP Authentication";
    }
}
