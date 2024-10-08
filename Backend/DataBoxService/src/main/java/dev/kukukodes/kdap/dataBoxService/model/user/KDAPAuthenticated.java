package dev.kukukodes.kdap.dataBoxService.model.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Collection;
import java.util.List;

/**
 * Authentication with user data and authenticated set to true.
 */
@Getter
@RequestScope
@RequiredArgsConstructor
public class KDAPAuthenticated implements Authentication {
    private final KDAPUser user;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.getId()));
    }

    @Override
    public Object getCredentials() {
        return user.getId();
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
        return user.getName();
    }
}
