package dev.kukukodes.kdap.authenticationservice.models.userModels;

import dev.kukukodes.kdap.authenticationservice.enums.UserRole;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A KDAP User that implements authentication and can either be authenticated or not
 */
@Slf4j
@Getter
@Setter
public class KDAPUserAuthentication implements Authentication {
    //The token that was used to extract user data
    private final String jwtToken;
    private final String id;
    private UserRole userRole;
    private final boolean authenticated;

    List<GrantedAuthority> authorities;

    public KDAPUserAuthentication(String jwtToken, String id, UserRole userRole, boolean authenticated) {
        this.jwtToken = jwtToken;
        this.id = id;
        this.userRole = userRole;
        this.authenticated = authenticated;
        authorities = new ArrayList<>();
        authorities.add(new KDAPUserAuthority(userRole, id));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getCredentials() {
        return this.jwtToken;
    }

    @Override
    public String getDetails() {
        return String.format("User with id %s and role %s", id, userRole);
    }

    @Override
    public String getPrincipal() {
        return id;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        log.info("Good effort but it's futile");
    }

    @Override
    public String getName() {
        return id;
    }
}
