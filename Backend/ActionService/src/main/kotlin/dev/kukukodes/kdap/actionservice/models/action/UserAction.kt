package dev.kukukodes.kdap.actionservice.models.action

import dev.kukukodes.kdap.actionservice.helper.action.ActionHelper
import dev.kukukodes.kdap.actionservice.helper.action.DefaultActionHelper
import dev.kukukodes.kdap.actionservice.models.ActionRuntimeEnvironment
import dev.kukukodes.kdap.actionservice.models.actionNode.ActionNode

class UserAction(
    private val inputStructure: Map<String, ActionInputOutputStructure>,
    private val outputStructure: Map<String, ActionInputOutputStructure>,
    private val actionNode: ActionNode
) : Action {
    override fun getInputStructure(): Map<String, ActionInputOutputStructure> {
        return inputStructure;
    }

    override fun getOutputStructure(): Map<String, ActionInputOutputStructure> {
        return outputStructure;
    }

    override fun getActionHelper(): ActionHelper {
        return DefaultActionHelper();
    }

    override fun execute(input: Map<String, Any>, env: ActionRuntimeEnvironment, outputSaveName: String?) {
        actionNode.getAction().execute(input, env, outputSaveName)
    }
}