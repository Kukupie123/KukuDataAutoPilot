@file:Suppress("UNCHECKED_CAST")

package dev.kukukodes.kdap.actionservice.models.actions

import dev.kukukodes.kdap.actionservice.models.actions.definedActions.DefinedActionBase
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ActionRunnerEngine {
    private val log: Logger = LoggerFactory.getLogger(ActionRunnerEngine::class.java)

    /**
     * Executes the given InnerAction using the provided storage.
     * It first resolves input values based on the plug-in mapping and passes them to the action.
     *
     * @param action InnerAction to execute
     * @param storage Storage map to use during execution
     * @return The result as a map or null if there is no result
     */
    fun executeAction(action: InnerAction, storage: Map<String, Any?>): Map<String, Any?>? {
        // Resolve input values using the plug-in map
        val inputValues = resolveInputValues(storage, action.actionConnection.plugInMap)
        // Execute the action with the resolved input values
        return executeAction(action.action, inputValues)
    }

    /**
     * Executes the given action with the provided input values.
     * This method handles both DefinedActionBase and UserAction types.
     *
     * @param action The Action to execute
     * @param input The input values to pass to the action
     * @return A map with the result or null
     */
    private fun executeAction(action: Action, input: Map<String, Any?>): Map<String, Any?>? {
        if (action is DefinedActionBase) {
            // Map the input values according to the plug-in mapping and execute the action
            val inputs = mutableMapOf<String, Any?>()
            for (plugInKey in action.plugIn.keys) {
                inputs[plugInKey] = input[plugInKey]
            }
            return action.execute(inputs)
        } else if (action is UserAction) {
            // Execute the UserAction and return the result
            return executeUserAction(action, input)
        }
        return null
    }

    /**
     * Executes a UserAction by processing all the inner actions it contains.
     * For each inner action, it resolves input values, executes the action, and stores the result in scoped storage.
     *
     * @param userAction The UserAction to execute
     * @param input The initial input values
     * @return The final output map according to the output mapping
     */
    private fun executeUserAction(userAction: UserAction, input: Map<String, Any?>): Map<String, Any?> {
        // Create a mutable storage map to store temporary data
        val scopedStorage = input.toMutableMap()

        // Loop through each inner action in the UserAction
        for (innerAction in userAction.actions) {
            // Resolve the input values for the inner action
            val inputValues = resolveInputValues(scopedStorage, innerAction.actionConnection.plugInMap)
            // Execute the inner action
            val output = executeAction(innerAction.action, inputValues)

            // If the action produces an output, map it to the scopedStorage using plugOutMap
            if (output != null && innerAction.actionConnection.plugOutMap.isNotEmpty()) {
                for ((outputKey, storageKey) in innerAction.actionConnection.plugOutMap) {
                    if (!(storageKey.startsWith("{") && storageKey.endsWith("}"))) {
                        throw Exception("Invalid storage key while storing output")
                    }
                    // Get the value from the output using outputKey
                    val outputValue = resolveInput(outputKey, output)
                    // Store the result in scopedStorage according to storageKey
                    storeNestedValue(scopedStorage, storageKey, outputValue)
                }
            }
        }
        // Prepare the final output map according to UserAction's output mapping
        return resolveInputValues(scopedStorage, userAction.outputMap)
    }

    /**
     * Resolves input values from storage using the provided plug-in map.
     *
     * @param storage The storage map
     * @param plug The plug-in map that defines mappings from storage keys to action parameters
     * @return A map of resolved input values
     */
    private fun resolveInputValues(storage: Map<String, Any?>, plug: Map<String, String>): Map<String, Any?> {
        val resolvedValues = mutableMapOf<String, Any?>()
        // Loop through each entry in the plug-in map and resolve the input values from storage
        for ((key, value) in plug) {
            resolvedValues[key] = resolveInput(value, storage)
        }
        return resolvedValues
    }

    /**
     * Resolves an input expression, which can either be a literal value or a reference to a storage key.
     * If the expression is a storage reference (e.g., "{key.subkey}"), it navigates through the storage map.
     *
     * @param inputExpression The input expression to resolve
     * @param storage The storage map to use for resolving the expression
     * @return The resolved value or the literal value
     */
    private fun resolveInput(inputExpression: String, storage: Map<String, Any?>): Any? {
        log.info("Resolving input expression: $inputExpression for storage: $storage")
        // Check if the expression is a reference to a storage key (e.g., "{key.subkey}")
        if (inputExpression.startsWith("{") && inputExpression.endsWith("}")) {
            val keys = inputExpression.substring(1, inputExpression.length - 1).split(".")
            var currentMap: Any? = storage
            // Navigate through the nested storage map
            for (key in keys) {
                currentMap = (currentMap as? Map<String, Any?>)?.get(key)
                if (currentMap == null) {
                    return null // Return null if any level is missing
                }
            }
            return currentMap
        }
        // If it's a literal value, return it directly
        return inputExpression
    }

    /**
     * Stores a value in a nested map structure based on the storage key expression.
     *
     * @param storage The storage map where the value should be stored
     * @param storageKey The storage key expression (e.g., "{key.subkey}")
     * @param value The value to store
     */
    private fun storeNestedValue(storage: MutableMap<String, Any?>, storageKey: String, value: Any?) {
        val keys = storageKey.substring(1, storageKey.length - 1).split(".")
        var currentMap: MutableMap<String, Any?> = storage
        // Navigate through the nested map structure
        for (i in 0 until keys.size - 1) {
            // If the key is not present, create a new nested map
            if (currentMap[keys[i]] !is MutableMap<*, *>) {
                currentMap[keys[i]] = mutableMapOf<String, Any?>()
            }
            currentMap = currentMap[keys[i]] as MutableMap<String, Any?>
        }
        // Store the final value in the last key
        currentMap[keys.last()] = value
    }
}
