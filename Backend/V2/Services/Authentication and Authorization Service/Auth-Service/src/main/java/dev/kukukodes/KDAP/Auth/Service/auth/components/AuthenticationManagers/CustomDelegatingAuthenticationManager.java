package dev.kukukodes.KDAP.Auth.Service.auth.components.AuthenticationManagers;

import dev.kukukodes.KDAP.Auth.Service.auth.constants.AuthConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.DelegatingReactiveAuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.stereotype.Component;

/**
 * Delegating Authentication Manager that delegates authentication attempts to multiple custom Authentication Managers. <br>
 * It sequentially attempts to authenticate using each provided Authentication Manager until one of them succeeds, or all fail. <br>
 * The purpose of using a DelegatingAuthenticationManager is to handle multiple authentication methods (e.g., user/password, OAuth)
 * within the same authentication flow. <br>
 * A primary Authentication Manager is needed because Spring Security requires a single instance of AuthenticationManager
 * for handling authentication; if multiple instances are present, Spring won't know which one to use. <br>
 * Qualifier annotations alone won't work because Spring Security's internal mechanism uses constructor injection,
 * so the DelegatingAuthenticationManager combines multiple managers into a unified manager.
 */

@Primary
@Component
public class CustomDelegatingAuthenticationManager extends DelegatingReactiveAuthenticationManager {

    @Autowired
    public CustomDelegatingAuthenticationManager(
            @Qualifier(AuthConstants.CustomAuthenticationManagerQualifier.USER_PASSWORD) ReactiveAuthenticationManager userPwdAuthenticationManager,
            @Qualifier(AuthConstants.CustomAuthenticationManagerQualifier.OAUTH) ReactiveAuthenticationManager oAuthAuthenticationManager
    ) {
        super(userPwdAuthenticationManager, oAuthAuthenticationManager);
    }
}
