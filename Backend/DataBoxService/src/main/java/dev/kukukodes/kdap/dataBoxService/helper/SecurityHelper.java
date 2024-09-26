package dev.kukukodes.kdap.dataBoxService.helper;

import dev.kukukodes.kdap.dataBoxService.model.user.KDAPAuthenticated;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityHelper {
    public KDAPAuthenticated getCurrentUser() {
        return (KDAPAuthenticated) SecurityContextHolder.getContext().getAuthentication();
    }
}
