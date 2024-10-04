package dev.kukukodes.kdap.actionservice.models.actions

import dev.kukukodes.kdap.actionservice.models.actions.plug.ActionPlug

class UserAction(
    override val name: String, override val description: String,
    override val plugIn: Map<String, ActionPlug>,
    override val plugOut: Map<String, ActionPlug>,
    /**
     * Unique identifier for each action. Necessary for mapping plugs
     */
    val actions: List<InnerAction>,
    /**
     * The key where the result is stored in "local storage"
     */
    val storageResultKey : String
) : Action {
    override fun execute(input: Map<String, Any?>): Map<String, Any?> {
        TODO("Not yet implemented")
    }
}