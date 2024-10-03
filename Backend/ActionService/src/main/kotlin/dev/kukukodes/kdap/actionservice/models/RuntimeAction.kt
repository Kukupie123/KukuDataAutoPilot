package dev.kukukodes.kdap.actionservice.models

import lombok.ToString

/**
 * Action that is being "Executed" with required parameters such as input values and saveName for output
 */
@ToString
class RuntimeAction(
    private val action: ActionDefinition,
    private val inputMap: Map<String, Any>,
    private val saveName: String,
    private val runtimeStorage: MutableMap<String, Any>
) {

    /**
     * Resolve inputMap to get literal values that need to be passed to action as input.
     * This function checks for references in the inputMap and resolves them against runtimeStorage.
     */
    private fun resolveInputMap(
        inputStructure: Map<String, Any> = action.inputStructure,
        inputMap: Map<String, Any> = this.inputMap,
    ): Map<String, Any> {
        val inputMapValue = mutableMapOf<String, Any>()

        // Iterate over inputStructure to resolve actual values from inputMap
        for ((fieldName, fieldType) in inputStructure) {
            val inputValExp = inputMap[fieldName]
                ?: throw IllegalArgumentException("Input value for field $fieldName is missing.")

            var resolvedValue: Any

            if (fieldType is String) {
                if (inputValExp is String && inputValExp.startsWith("{") && inputValExp.endsWith("}")) {
                    // Resolve value from runtimeStorage (handling nested access)
                    val keys = inputValExp.substring(1, inputValExp.length - 1).split(".")
                    resolvedValue = resolveFromStorage(keys)
                } else {
                    // Direct literal value
                    resolvedValue = inputValExp
                }

                // Convert resolved value to the expected type
                when (fieldType) {
                    "INTEGER" -> resolvedValue = resolvedValue.toString().toIntOrNull()
                        ?: throw IllegalArgumentException("Expected INTEGER value for $fieldName but got $resolvedValue")

                    "DECIMAL" -> resolvedValue = resolvedValue.toString().toDoubleOrNull()
                        ?: throw IllegalArgumentException("Expected DECIMAL value for $fieldName but got $resolvedValue")

                    "TEXT" -> resolvedValue = resolvedValue.toString()
                    "BOOLEAN" -> resolvedValue = resolvedValue.toString().toBoolean()
                }
                inputMapValue[fieldName] = resolvedValue
            } else if (fieldType is Map<*, *>) {
                if (inputValExp !is Map<*, *>) {
                    throw IllegalArgumentException("Expected a map for field $fieldName but got ${inputValExp::class.simpleName}")
                }
                val nestedInputStructure = fieldType as Map<String, Any>
                val nestedInputMap = inputValExp as Map<String, Any>
                inputMapValue[fieldName] = resolveInputMap(nestedInputStructure, nestedInputMap)
            }
        }
        return inputMapValue
    }

    /**
     * Resolve a value from runtimeStorage using keys. Supports nested structure access.
     */
    private fun resolveFromStorage(keys: List<String>): Any {
        var currentMap: Any = runtimeStorage
        for (key in keys) {
            if (currentMap is Map<*, *>) {
                currentMap = currentMap[key] ?: throw IllegalArgumentException("Key ($key) not found in runtimeStorage")
            } else {
                throw IllegalArgumentException("Invalid storage path at key $key")
            }
        }
        return currentMap
    }

    /**
     * Execute the action using the resolved input and store the result in runtimeStorage.
     */
    fun executeAction() {
        // Validate input map before resolving
        //validateInputMap()
        //TODO : Fix validate input. It needs to validate input value not map

        val resolvedInputs = resolveInputMap()
        val output = action.execute(resolvedInputs)

        // Store the output in runtimeStorage under the specified saveName
        if(!output.isNullOrEmpty()) {
            runtimeStorage[saveName] = output
        }
    }
    override fun toString(): String {
        return "RuntimeAction(action=$action, inputMap=$inputMap, saveName='$saveName', runtimeStorage=$runtimeStorage)"
    }
    //TODO: We need output map similar to input map to map return values. Execute function also needs to be modified to handle this.
}
