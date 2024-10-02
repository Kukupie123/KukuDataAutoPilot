package dev.kukukodes.kdap.actionservice.models

import dev.kukukodes.kdap.actionservice.models.action.SystemAction
import dev.kukukodes.kdap.actionservice.models.action.UserAction
import dev.kukukodes.kdap.actionservice.models.actionNode.ActionNode

/**
 * An environment that action and action nodes will have access to when they are "executing"
 */
class ActionRuntimeEnvironment(
    private val initialInput: Map<String, Any>,
    val storage: HashMap<String, Any>,
    private val initialNode: ActionNode
) {

    fun start() {
        if (initialNode.getAction() is SystemAction) {
            val sysAction = initialNode.getAction() as SystemAction
            sysAction.execute(initialInput, this);
        } else {
        }
    }
}