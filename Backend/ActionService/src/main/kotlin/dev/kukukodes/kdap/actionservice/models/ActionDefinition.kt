package dev.kukukodes.kdap.actionservice.models

import org.slf4j.Logger
import org.slf4j.LoggerFactory

open class ActionDefinition(
    val id: String,
    val inputStructure: Map<String, Any>,
    val outputStructure: Map<String, Any>,
    val execution: List<RuntimeAction>
) {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    /**
     * "Execute" the action.
     * @param inputValue Input values which needs to match inputStructure
     * @return output value
     */
    open fun execute(inputValue: Map<String, Any>): Map<String, Any>? {
        for (action in execution) {
            log.info("Executing Action $action")
            action.executeAction()
        }
        return null;
    }

    override fun toString(): String {
        return "ActionDefinition(id='$id', inputStructure=$inputStructure, outputStructure=$outputStructure, execution=$execution, log=$log)"
    }


}
