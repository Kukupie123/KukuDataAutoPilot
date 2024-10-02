package dev.kukukodes.kdap.actionservice.models.action

interface Action {
    fun getInputStructure(): Map<String, Any>
    fun getOutputStructure(): Map<String, Any>

    /**
     * @param runtimeStorage The runtime storage
     * @param inputValues Actual input values. Needs to match inputStructure
     * @param outputName The key that will be used to store the output in runtime storage
     */
    fun execute(runtimeStorage: HashMap<String, Any>, inputValues: Map<String, Any>, outputName: String)
}