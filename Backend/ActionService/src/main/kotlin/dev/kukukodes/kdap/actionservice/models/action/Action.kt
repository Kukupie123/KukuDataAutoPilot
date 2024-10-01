package dev.kukukodes.kdap.actionservice.models.action

/**
 * Action is an action that performs different operations which can be system defined or user defined.
 */
interface Action {
    fun getInputStructure();
    fun getOutputStructure();
}