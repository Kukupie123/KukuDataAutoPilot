package dev.kukukodes.kdap.actionservice.models.action

import dev.kukukodes.kdap.actionservice.helper.action.ActionHelper
import dev.kukukodes.kdap.actionservice.models.ActionRuntimeEnvironment

/**
 * Action is an action that performs different operations which can be system defined or user defined.
 */
interface Action {
    fun getInputStructure(): Map<String, ActionInputOutputStructure>
    fun getOutputStructure(): Map<String, ActionInputOutputStructure>
    fun getActionHelper(): ActionHelper
    fun execute(input: Map<String, Any>, env: ActionRuntimeEnvironment, outputSaveName: String?)

    //TODO: Input and output processor so that we can pass "name":"storage.output1.name"
}