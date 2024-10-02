package dev.kukukodes.kdap.actionservice.helper.action

import dev.kukukodes.kdap.actionservice.models.action.ActionInputOutputStructure

interface ActionHelper {
    fun validateInputOutputStructure(
        structure: Map<String, ActionInputOutputStructure>,
        value: Map<String, Any>
    ): Boolean
}