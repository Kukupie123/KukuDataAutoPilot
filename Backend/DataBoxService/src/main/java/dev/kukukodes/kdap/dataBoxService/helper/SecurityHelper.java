package dev.kukukodes.kdap.dataBoxService.helper;

import dev.kukukodes.kdap.dataBoxService.constants.AccessLevelConst;
import dev.kukukodes.kdap.dataBoxService.model.user.KDAPAuthenticated;
import dev.kukukodes.kdap.dataBoxService.openFeign.AuthenticationComs;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.nio.file.AccessDeniedException;

@Component
@RequiredArgsConstructor
@Log4j2
public class SecurityHelper {
    private final AuthenticationComs authenticationComs;
    private final ServiceCommunicationHelper serviceCommunicationHelper;

    public KDAPAuthenticated getCurrentUser() {
        return (KDAPAuthenticated) SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * If the current use match the passed user or is an amin the function will complete without throwing exception
     * @param userId user operating on
     */
    public void validateAccess(String userId) throws AccessDeniedException {
        KDAPAuthenticated currentUser = getCurrentUser();
        if (!currentUser.getUser().getAccessLevel().equals(AccessLevelConst.ADMIN) &&
                !userId.equals(currentUser.getUser().getId())) {
            log.info("Access denied. Attempting to access data for userID {} while current user is {}", userId, currentUser.getUser().getId());
            throw new AccessDeniedException("Access denied");
        }
    }


}
