package dev.kukukodes.kdap.actionservice.models.actions

import dev.kukukodes.kdap.actionservice.models.actions.plug.ActionPlug

class UserAction(
    override val name: String,
    override val description: String = "",
    override val plugIn: Map<String, ActionPlug>,
    override val plugOut: Map<String, ActionPlug>,
    /**
     * Unique identifier for each action. Necessary for mapping plugs
     */
    val actions: List<InnerAction>,
    /**
     * Map the outputs returned by the inner actions into output plug
     */
    val outputMap: Map<String, String>
) : Action