package dev.kukukodes.kdap.actionservice.models

/**
 * Action that is being "Executed" with required parameters such as input values and saveName for output
 */
class RuntimeAction(
    private val action: ActionDefinition,
    private val inputMap: Map<String, Any>,
    private val saveName: String,
    private val runtimeStorage: MutableMap<String, Any>
) {

    /**
     * Validate the inputMap against the input structure of the action.
     * Ensure that the inputMap contains the correct keys and that values conform to expected types.
     */
    private fun validateInputMap() {
        val inputStructure = action.inputStructure

        // Iterate over the input structure to validate each field
        for ((fieldName, fieldType) in inputStructure) {
            val inputValue = inputMap[fieldName]
                ?: throw IllegalArgumentException("Missing input field: $fieldName")

            when (fieldType) {
                is String -> {
                    if (fieldType !in listOf("INTEGER", "DECIMAL", "BOOLEAN", "TEXT")) {
                        throw IllegalArgumentException("Invalid field type: $fieldType for field $fieldName")
                    }
                    // Check value type based on expected input field type
                    when (fieldType) {
                        "INTEGER" -> inputValue.toString().toIntOrNull()
                            ?: throw IllegalArgumentException("Expected INTEGER value for field $fieldName")

                        "DECIMAL" -> inputValue.toString().toDoubleOrNull()
                            ?: throw IllegalArgumentException("Expected DECIMAL value for field $fieldName")

                        "BOOLEAN" -> if (inputValue !is Boolean && inputValue !is String)
                            throw IllegalArgumentException("Expected BOOLEAN value for field $fieldName")

                        "TEXT" -> if (inputValue !is String)
                            throw IllegalArgumentException("Expected TEXT value for field $fieldName")
                    }
                }

                is Map<*, *> -> {
                    if (inputValue !is Map<*, *>) {
                        throw IllegalArgumentException("Expected a map for field $fieldName")
                    }
                    // Recursively validate the nested map structure
                    validateNestedInputMap(fieldType as Map<String, Any>, inputValue as Map<String, Any>)
                }

                else -> throw IllegalArgumentException("Unknown field type for $fieldName")
            }
        }
    }

    /**
     * Validate nested input maps recursively.
     */
    private fun validateNestedInputMap(nestedStructure: Map<String, Any>, nestedMap: Map<String, Any>) {
        for ((nestedFieldName, nestedFieldType) in nestedStructure) {
            val nestedInputValue = nestedMap[nestedFieldName]
                ?: throw IllegalArgumentException("Missing input field: $nestedFieldName")

            when (nestedFieldType) {
                is String -> {
                    if (nestedFieldType !in listOf("INTEGER", "DECIMAL", "BOOLEAN", "TEXT")) {
                        throw IllegalArgumentException("Invalid field type: $nestedFieldType for field $nestedFieldName")
                    }
                    when (nestedFieldType) {
                        "INTEGER" -> nestedInputValue.toString().toIntOrNull()
                            ?: throw IllegalArgumentException("Expected INTEGER value for nested field $nestedFieldName")

                        "DECIMAL" -> nestedInputValue.toString().toDoubleOrNull()
                            ?: throw IllegalArgumentException("Expected DECIMAL value for nested field $nestedFieldName")

                        "BOOLEAN" -> if (nestedInputValue !is Boolean && nestedInputValue !is String)
                            throw IllegalArgumentException("Expected BOOLEAN value for nested field $nestedFieldName")

                        "TEXT" -> if (nestedInputValue !is String)
                            throw IllegalArgumentException("Expected TEXT value for nested field $nestedFieldName")
                    }
                }

                is Map<*, *> -> {
                    if (nestedInputValue !is Map<*, *>) {
                        throw IllegalArgumentException("Expected a map for nested field $nestedFieldName")
                    }
                    // Recursively validate nested structure
                    validateNestedInputMap(nestedFieldType as Map<String, Any>, nestedInputValue as Map<String, Any>)
                }
            }
        }
    }

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
                        ?: throw IllegalArgumentException("Expected INTEGER value for $fieldName")

                    "DECIMAL" -> resolvedValue = resolvedValue.toString().toDoubleOrNull()
                        ?: throw IllegalArgumentException("Expected DECIMAL value for $fieldName")

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
                currentMap = currentMap[key] ?: throw IllegalArgumentException("Key $key not found in runtimeStorage")
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
        validateInputMap()

        val resolvedInputs = resolveInputMap()
        val output = action.execute(resolvedInputs)

        // Store the output in runtimeStorage under the specified saveName
        runtimeStorage[saveName] = output
    }
}
