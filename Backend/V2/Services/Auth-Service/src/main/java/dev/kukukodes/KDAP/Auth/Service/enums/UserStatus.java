package dev.kukukodes.KDAP.Auth.Service.enums;

import lombok.Getter;

/**
 * Enum representing the status of a user in the system.
 */
@Getter
public enum UserStatus {
    ACTIVE("active"),
    INACTIVE("inactive"),
    BANNED("banned"),
    PENDING_VERIFICATION("pendingVerification"),
    VERIFIED("verified"),
    VERIFICATION_REJECTED("verificationRejected");

    private final String status;

    UserStatus(String status) {
        this.status = status;
    }

    public static UserStatus fromString(String status) {
        for (UserStatus userStatus : UserStatus.values()) {
            if (userStatus.status.equalsIgnoreCase(status)) {
                return userStatus;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + status);
    }
}
