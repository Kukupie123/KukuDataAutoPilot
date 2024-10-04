package dev.kukukodes.kdap.actionservice.models.actions

import dev.kukukodes.kdap.actionservice.models.actions.plug.ActionPlug

interface Action {
    val name: String
    val description: String
    val plugIn: Map<String, ActionPlug>
    val plugOut: Map<String, ActionPlug>
    fun execute(input: Map<String, Any?>): Map<String, Any?>
}