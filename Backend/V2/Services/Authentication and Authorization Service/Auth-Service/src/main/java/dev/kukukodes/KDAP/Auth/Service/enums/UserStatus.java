package dev.kukukodes.KDAP.Auth.Service.enums;

import lombok.Getter;

/**
 * Enum representing the status of a user in the system.
 */
@Getter
public enum UserStatus {
    ACTIVE, INACTIVE, BANNED, PENDING_VERIFICATION, VERIFIED, VERIFICATION_REJECTED
}
