package dev.kukukodes.kdap.actionservice.models.action.systemactions

import dev.kukukodes.kdap.actionservice.helper.action.ActionHelper
import dev.kukukodes.kdap.actionservice.helper.action.DefaultActionHelper
import dev.kukukodes.kdap.actionservice.models.ActionRuntimeEnvironment
import dev.kukukodes.kdap.actionservice.models.action.ActionInputOutputStructure
import dev.kukukodes.kdap.actionservice.models.action.SystemAction

class LinearAction : SystemAction("LinearAction") {
    override fun getInputStructure(): Map<String, ActionInputOutputStructure> {
        return mapOf(
            "actionID" to ActionInputOutputStructure.TEXT,
            "nextActionID" to ActionInputOutputStructure.TEXT
        )
    }

    override fun getOutputStructure(): Map<String, ActionInputOutputStructure> {
        return mapOf();
    }

    override fun getActionHelper(): ActionHelper {
       return DefaultActionHelper()
    }

    override fun execute(input: Map<String, Any>, env: ActionRuntimeEnvironment, outputSaveName: String?) {
        TODO("Not yet implemented")
    }
}