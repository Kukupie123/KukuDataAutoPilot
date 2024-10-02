package dev.kukukodes.kdap.actionservice.models.action

import dev.kukukodes.kdap.actionservice.models.ActionRuntimeEnvironment

/**
 * Pre-defined actions that can be used by user actions
 */
abstract class SystemAction(
    /**
     * Used to identify different system actions
     */
    val identifier: String
) : Action