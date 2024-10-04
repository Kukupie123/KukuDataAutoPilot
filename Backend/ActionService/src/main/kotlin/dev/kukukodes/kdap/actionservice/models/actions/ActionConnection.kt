package dev.kukukodes.kdap.actionservice.models.actions

/**
 * @param plugInMap Action PlugIn name to Mapped PlugIn Name.
 * @param plugOutMap Action PlugOut name to Mapped PlugOut Name
 */
data class ActionConnection(val plugInMap: Map<String, String>, val plugOutMap: Map<String, String>) {
}