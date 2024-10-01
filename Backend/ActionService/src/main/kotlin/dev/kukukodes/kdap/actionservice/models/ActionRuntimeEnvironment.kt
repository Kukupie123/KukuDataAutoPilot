package dev.kukukodes.kdap.actionservice.models

import dev.kukukodes.kdap.actionservice.models.actionNode.ActionNode
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

/**
 * An environment that action and action nodes will have access to when they are "executing"
 */
class ActionRuntimeEnvironment(val storage: HashMap<String, JvmType.Object>, val initialNode: ActionNode) {
}