package dev.kukukodes.KDAP.Auth.constants.auth;

public class AuthConstants {

    public static final class CustomAuthenticationManagerQualifier {
        public static final String USER_PASSWORD = "CUSTOM_AUTH_QUALIFIER_USER_PASSWORD";
        public static final String OAUTH = "CUSTOM_AUTH_QUALIFIER_OAUTH";
    }

    public static final class RootUser {
        public static final String ENV_RootUserName = "RootUserName";
        public static final String ENV_RootUserPassword = "RootUserPassword";
        public static final String ENV_RootRoleName = "RootUserRole";
        public static final String ENV_RootUserPermission = "RootUserPermission";
        public static final String ENV_RootUserOperation = "RootUserOperation";
    }
}
