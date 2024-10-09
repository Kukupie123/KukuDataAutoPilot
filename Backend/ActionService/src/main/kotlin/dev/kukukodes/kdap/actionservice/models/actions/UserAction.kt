package dev.kukukodes.kdap.actionservice.models.actions

import dev.kukukodes.kdap.actionservice.models.actions.definedActions.DefinedActionBase
import dev.kukukodes.kdap.actionservice.models.actions.plug.ActionPlug

class UserAction(
    override val name: String,
    override val description: String,
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
) : Action {
    fun execute(storage: Map<String, Any?>): Map<String, Any?> {
        val ls = storage.toMutableMap()
        for (a in actions) {
            val inputVals = mutableMapOf<String, Any?>()
            for (ac in a.actionConnection.plugInMap) {
                inputVals[ac.key] = ls[ac.value]
            }
            var output = mapOf<String, Any?>()
            if (a.action is DefinedActionBase) {
                output = a.action.execute(inputVals)
            } else if (a.action is UserAction) {
                output = a.action.execute(storage)
            }

            for (ac in a.actionConnection.plugOutMap) {
                ls[ac.value] = output[ac.key]
            }
        }
        val finalOutput = mutableMapOf<String, Any?>()

        for (plug in plugOut) {
            val lsVal = ls[outputMap[plug.key]] //Access LS->PlugKey whose value is the output
            finalOutput[plug.key] = lsVal
        }
        return finalOutput;
    }
}