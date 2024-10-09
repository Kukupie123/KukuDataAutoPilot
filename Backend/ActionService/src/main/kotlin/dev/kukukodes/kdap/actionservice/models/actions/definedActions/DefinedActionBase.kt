package dev.kukukodes.kdap.actionservice.models.actions.definedActions

import dev.kukukodes.kdap.actionservice.models.actions.Action
import dev.kukukodes.kdap.actionservice.models.actions.plug.ActionPlug

abstract class DefinedActionBase(
    override val name: String,
    override val description: String,
    override val plugIn: Map<String, ActionPlug>,
    override val plugOut: Map<String, ActionPlug>,
) : Action {
    abstract fun execute(input: Map<String, Any?>): Map<String, Any?>
}