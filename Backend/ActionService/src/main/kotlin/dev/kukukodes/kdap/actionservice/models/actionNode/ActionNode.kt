package dev.kukukodes.kdap.actionservice.models.actionNode

import dev.kukukodes.kdap.actionservice.models.action.Action

/**
 * Action node holds actions and reference to the next action node.
 * It's used to create a flow of action.
 */
interface ActionNode {
    fun getAction(): Action;
    fun getNextNode(): ActionNode?;
    fun execute();
}