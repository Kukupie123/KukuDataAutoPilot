package dev.kukukodes.kdap.actionservice.models.action.systemactions

import dev.kukukodes.kdap.actionservice.helper.action.ActionHelper
import dev.kukukodes.kdap.actionservice.helper.action.DefaultActionHelper
import dev.kukukodes.kdap.actionservice.models.ActionRuntimeEnvironment
import dev.kukukodes.kdap.actionservice.models.action.ActionInputOutputStructure
import dev.kukukodes.kdap.actionservice.models.action.SystemAction

class GetContainerVal : SystemAction("GetContainerVal") {
    override fun execute(input: Map<String, Any>, env: ActionRuntimeEnvironment, outputSaveName: String?) {
        val valName = input["name"] as String
        val value = env.storage[valName]
        if (value != null && outputSaveName != null) {
            env.storage[outputSaveName] = mapOf("name" to value)
        }
    }

    override fun getInputStructure(): Map<String, ActionInputOutputStructure> {
        return mapOf(
            "name" to ActionInputOutputStructure.TEXT
        )
    }

    override fun getOutputStructure(): Map<String, ActionInputOutputStructure> {
        return mapOf("value" to ActionInputOutputStructure.TEXT);
    }

    override fun getActionHelper(): ActionHelper {
        return DefaultActionHelper();
    }
}