package dev.kukukodes.kdap.authenticationservice.models;

import dev.kukukodes.kdap.authenticationservice.entity.user.KDAPUserEntity;
import dev.kukukodes.kdap.authenticationservice.enums.AuthAccessLevel;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/**
 * Holds data about the session such as current user, authority, etc
 */
@Component
@RequestScope
public class SessionData {
    ///Holds the current user based on user extracted from jwt token. Will be null if it's service request
    private KDAPUserEntity currentUser;
    ///Holds the access level of current user or service that is requesting
    private AuthAccessLevel accessLevel;
}
