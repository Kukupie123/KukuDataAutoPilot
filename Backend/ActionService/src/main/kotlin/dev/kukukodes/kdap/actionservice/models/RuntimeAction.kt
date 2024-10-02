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
     * Resolve inputMap to get literal values that need to be passed to action as input.
     * This function checks for references in the inputMap and resolves them against runtimeStorage.
     */
    private fun resolveInputMap(): Map<String, Any> {
        val inputVal = emptyMap<String, Any>();
        if (!validateInputMapStructure(inputVal)) {
            throw Exception("Invalid structure")
        }
        return inputVal
    }

    private fun validateInputMapStructure(inputValue: Map<String, Any>): Boolean {
        return false;
    }

    /**
     * Execute the action using the resolved input and store the result in runtimeStorage.
     */
    fun executeAction() {
        val resolvedInputs = resolveInputMap()
        val output = action.execute(resolvedInputs)
        // Store the output in runtimeStorage under the specified saveName
        runtimeStorage[saveName] = output
    }
}
