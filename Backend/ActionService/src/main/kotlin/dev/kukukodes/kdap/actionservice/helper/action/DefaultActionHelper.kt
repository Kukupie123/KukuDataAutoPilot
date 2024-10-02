package dev.kukukodes.kdap.actionservice.helper.action

import dev.kukukodes.kdap.actionservice.models.action.ActionInputOutputStructure
import org.springframework.stereotype.Component

@Component
class DefaultActionHelper : ActionHelper {

    override fun validateInputOutputStructure(
        structure: Map<String, ActionInputOutputStructure>,
        value: Map<String, Any>
    ): Boolean {
        return false;
    }
}