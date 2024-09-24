package dev.kukukodes.kdap.dataBoxService.helper;

import dev.kukukodes.kdap.dataBoxService.model.KDAPAuthenticatedUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityHelper {
    public KDAPAuthenticatedUser getCurrentUser() {
        return (KDAPAuthenticatedUser) SecurityContextHolder.getContext().getAuthentication();
    }
}
