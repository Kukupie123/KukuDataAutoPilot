package dev.kukukodes.KDAP.Auth.auth.models;

import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Custom implementation of {@link UserDetails} to provide user-specific details for authentication and authorization.
 * <p>
 * This class encapsulates user information and implements the {@link UserDetails} interface required by Spring Security.
 * It provides methods to access user details such as username, password, and authorities, as well as various status indicators
 * for account management.
 * </p>
 */
@Slf4j
@AllArgsConstructor
@ToString
public class CustomUserDetails implements UserDetails {

    private final int id;
    private final String userID;
    private final String hashedPassword;

    /**
     * Returns the authorities granted to the user. In this implementation, no authorities are granted.
     *
     * @return an empty list of {@link GrantedAuthority} as no authorities are assigned.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    /**
     * Returns the password of the user.
     *
     * @return the hashed password of the user.
     */
    @Override
    public String getPassword() {
        return hashedPassword;
    }

    /**
     * Returns the username of the user.
     *
     * @return the user ID (username) of the user.
     */
    @Override
    public String getUsername() {
        return userID;
    }

    /**
     * Returns the unique identifier of the user.
     *
     * @return the ID of the user.
     */
    public int getUserID() {
        return id;
    }

    /**
     * Indicates whether the user's account is non-expired.
     * <p>
     * This method uses the default implementation from {@link UserDetails} which returns true. If custom expiration logic
     * is required, this method should be overridden.
     * </p>
     *
     * @return true if the account is non-expired, false otherwise.
     */
    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    /**
     * Indicates whether the user's account is non-locked.
     * <p>
     * This method uses the default implementation from {@link UserDetails} which returns true. If custom lock logic is
     * required, this method should be overridden.
     * </p>
     *
     * @return true if the account is non-locked, false otherwise.
     */
    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    /**
     * Indicates whether the user's credentials are non-expired.
     * <p>
     * This method uses the default implementation from {@link UserDetails} which returns true. If custom credentials expiration
     * logic is required, this method should be overridden.
     * </p>
     *
     * @return true if the credentials are non-expired, false otherwise.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    /**
     * Indicates whether the user is enabled.
     * <p>
     * This method uses the default implementation from {@link UserDetails} which returns true. If custom enablement logic is
     * required, this method should be overridden.
     * </p>
     *
     * @return true if the user is enabled, false otherwise.
     */
    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
