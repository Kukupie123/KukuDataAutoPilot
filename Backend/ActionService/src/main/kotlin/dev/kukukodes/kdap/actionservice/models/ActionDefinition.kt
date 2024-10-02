package dev.kukukodes.kdap.actionservice.models

data class ActionDefinition(
    val id: String,
    val inputStructure: Map<String, Any>,
    val outputStructure: Map<String, Any>,
    val execution: List<RuntimeAction>
) {
    /**
     * "Execute" the action.
     * @param inputValue Input values which needs to match inputStructure
     * @return output value
     */
    fun execute( inputValue: Map<String, Any>): Map<String, Any> {
        TODO("Complete")
    }
}