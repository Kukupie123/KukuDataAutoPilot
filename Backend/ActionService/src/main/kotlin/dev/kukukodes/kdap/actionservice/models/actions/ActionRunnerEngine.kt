@file:Suppress("UNCHECKED_CAST")

package dev.kukukodes.kdap.actionservice.models.actions

import dev.kukukodes.kdap.actionservice.models.actions.definedActions.DefinedActionBase
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ActionRunnerEngine {
    private val log: Logger = LoggerFactory.getLogger(ActionRunnerEngine::class.java)
    fun executeAction(action: InnerAction, storage: Map<String, Any?>): Map<String, Any?>? {
        val inputValues = parseValuesFromMap(storage, action.actionConnection.plugInMap)
        return executeActionPvt(action.action, inputValues)
    }

    private fun executeActionPvt(action: Action, storage: Map<String, Any?>): Map<String, Any?>? {
        if (action is DefinedActionBase) {
            //Defined Actions require the values to be present in the storage. It doesn't parse
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
            val inputValues = parseValuesFromMap(scopeStorage, innerAction.actionConnection.plugInMap)
            //Store output of action
            val output = executeActionPvt(innerAction.action, inputValues)
            // Map the output to a valid scopeStorage key using plugOutMap
            if (output != null && innerAction.actionConnection.plugOutMap.isNotEmpty()) {
                for ((outputKey, storageKey) in innerAction.actionConnection.plugOutMap) {
                    if (!(storageKey.startsWith("{") && storageKey.endsWith("}"))) {
                        throw Exception("Invalid storage key while storing output")
                    }
                    val outputVal = parseInput(outputKey, output)
                    val storageKeyMap = storageKey.substring(1, storageKey.length - 1).split(".")
                    if (storageKeyMap.size == 1) {
                        scopeStorage[storageKeyMap[0]] = outputVal
                    } else {
                        val maxLevel = storageKeyMap.size - 2
                        var nestedMap = scopeStorage
                        for (i in 0..maxLevel) {
                            //Check if key is already there, if not then create new mutableHashMap
                            if (!scopeStorage.containsKey(storageKeyMap[i])) {
                                scopeStorage[storageKeyMap[i]] = emptyMap<String, Any?>().toMutableMap()
                            }
                            nestedMap = scopeStorage[storageKeyMap[i]] as MutableMap<String, Any?>
                        }
                        nestedMap[storageKeyMap[storageKeyMap.size - 1]] = outputVal
                    }
                }
            }
        }
        // Prepare final output according to UserAction's output mapping
        return parseValuesFromMap(scopeStorage, userAction.outputMap)
    }

    private fun parseValuesFromMap(storage: Map<String, Any?>, plug: Map<String, String>): Map<String, Any?> {
        val inputMap = mutableMapOf<String, Any?>()
        for ((key, value) in plug) {
            inputMap[key] = parseInput(value, storage)
        }
        return inputMap
    }

    private fun parseInput(inputExpression: String, storage: Map<String, Any?>): Any? {
        log.info("Parsing input expression: $inputExpression for Storage $storage")
        if (inputExpression.startsWith("{") && inputExpression.endsWith("}")) {
            var updatedInputExpression = inputExpression.substring(1, inputExpression.length - 1)
            //It is an expression
            var levels = updatedInputExpression.split(".")
            if (levels.size == 1) {
                return storage[levels[0]]
            }
            //Access the first level sub map
            val subMap = storage[levels[0]] as Map<String, Any?>
            levels = levels.subList(1, levels.size) //Remove the first element because we are accessing it
            //Create new input expression without the first level
            updatedInputExpression = "{"
            updatedInputExpression += levels.joinToString(".")
            updatedInputExpression += "}"
            return parseInput(updatedInputExpression, subMap)
        }
        // Literal value
        return inputExpression
    }


}