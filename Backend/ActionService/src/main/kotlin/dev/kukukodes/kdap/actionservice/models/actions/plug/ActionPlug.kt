package dev.kukukodes.kdap.actionservice.models.actions.plug

sealed class ActionPlug {
    data class Primitive(val type: String, val defaultValue: Any?) : ActionPlug()
    data class Nest(val structure: Map<String, ActionPlug>, val defaultValue: Any?) : ActionPlug()
}
