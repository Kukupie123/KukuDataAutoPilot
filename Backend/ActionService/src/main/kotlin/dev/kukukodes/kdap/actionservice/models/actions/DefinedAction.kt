package dev.kukukodes.kdap.actionservice.models.actions

import dev.kukukodes.kdap.actionservice.models.actions.plug.ActionPlugDefinition

class DefinedAction(
    override val name: String, override val description: String,
    override val plugIn: Map<String, ActionPlugDefinition>,
    override val plugOut: Map<String, ActionPlugDefinition>,
    /**
     * Function that is to be executed
     */
    private val function: (Map<String, Any?>) -> Map<String, Any?>,
) : Action {
    override fun execute(input: Map<String, Any?>): Map<String, Any?> {
        return function(input)
    }
}