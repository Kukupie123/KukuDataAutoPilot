package dev.kukukodes.kdap.actionservice.models.actions

import dev.kukukodes.kdap.actionservice.models.actions.definedActions.DefinedActionBase

class ActionRunnerEngine {
    fun executeAction(action: Action, storage: Map<String, Any?>): Map<String, Any?>? {
        if (action is DefinedActionBase) {
            val inputs = mutableMapOf<String, Any?>()
            for (plugInMap in action.plugIn) {
                inputs[plugInMap.key] = storage[plugInMap.key]
            }
                return action.execute(inputs)
        } else if (action is UserAction) {
            return executeUserAction(action, storage)
        }
        return null
    }


    private fun executeUserAction(userAction: UserAction, input: Map<String, Any?>): Map<String, Any?> {
        val scopeStorage = input.toMutableMap()
        // Execute each inner action
        for (innerAction in userAction.actions) {
            //Get input values
            val inputValues = mutableMapOf<String, Any?>()
            for (con in innerAction.actionConnection.plugInMap) {
                inputValues[con.key] = scopeStorage[con.value]
            }
            val output = executeAction(innerAction.action, inputValues)
            // Store output in working storage using connection mapping
            for ((outputKey, storageKey) in innerAction.actionConnection.plugOutMap) {
                scopeStorage[storageKey] = output?.get(outputKey)
                    ?: throw IllegalStateException("Expected output key $outputKey not found in action result")
            }
        }
        // Prepare final output according to UserAction's output mapping
        val actionOutput = mutableMapOf<String, Any?>()
        for (outputMap in userAction.outputMap) {
            actionOutput[outputMap.key] = scopeStorage[outputMap.value]
        }
        return actionOutput
    }

}