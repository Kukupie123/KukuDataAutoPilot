package dev.kukukodes.kdap.actionservice.models.action.systemactions

import dev.kukukodes.kdap.actionservice.helper.action.ActionHelper
import dev.kukukodes.kdap.actionservice.helper.action.DefaultActionHelper
import dev.kukukodes.kdap.actionservice.models.ActionRuntimeEnvironment
import dev.kukukodes.kdap.actionservice.models.action.ActionInputOutputStructure
import dev.kukukodes.kdap.actionservice.models.action.SystemAction

class BinaryAction : SystemAction("BinaryAction") {
    override fun getInputStructure(): Map<String, ActionInputOutputStructure> {
        return mapOf(
            "left" to ActionInputOutputStructure.TEXT,
            "left-type" to ActionInputOutputStructure.TEXT,
            "right" to ActionInputOutputStructure.TEXT,
            "right-type" to ActionInputOutputStructure.TEXT,
            "operator" to ActionInputOutputStructure.TEXT
        )
    }

    override fun getOutputStructure(): Map<String, ActionInputOutputStructure> {
        return mapOf(
            "value" to ActionInputOutputStructure.ALL,
            "error" to ActionInputOutputStructure.TEXT
        )
    }

    override fun getActionHelper(): ActionHelper {
        return DefaultActionHelper()
    }

    override fun execute(input: Map<String, Any>, env: ActionRuntimeEnvironment, outputSaveName: String?) {
        if (outputSaveName == null) {
            env.storage["error"] = "Output save name is required"
            return
        }

        val left = input["left"]
        val right = input["right"]
        val leftType = input["left-type"] as? String
        val rightType = input["right-type"] as? String
        val operator = input["operator"] as? String

        if (left == null || right == null || leftType == null || rightType == null || operator == null) {
            env.storage[outputSaveName] = mapOf("error" to "Missing required inputs")
            return
        }

        try {
            val leftValue = convertToType(left, leftType)
            val rightValue = convertToType(right, rightType)

            val result = when (operator) {
                "+" -> add(leftValue, rightValue)
                "-" -> subtract(leftValue, rightValue)
                "*" -> multiply(leftValue, rightValue)
                "/" -> divide(leftValue, rightValue)
                else -> throw IllegalArgumentException("Unsupported operator: $operator")
            }

            env.storage[outputSaveName] = mapOf("value" to result)
        } catch (e: Exception) {
            env.storage[outputSaveName] = mapOf("error" to e.message)
        }
    }

    private fun convertToType(value: Any, type: String): Any {
        return when (type.lowercase()) {
            "integer" -> (value as? String)?.toInt() ?: (value as? Number)?.toInt()
            ?: throw IllegalArgumentException("Cannot convert $value to Integer")

            "decimal" -> (value as? String)?.toDouble() ?: (value as? Number)?.toDouble()
            ?: throw IllegalArgumentException("Cannot convert $value to Decimal")

            "text" -> value.toString()
            "boolean" -> (value as? String)?.toBoolean() ?: (value as? Boolean)
            ?: throw IllegalArgumentException("Cannot convert $value to Boolean")

            else -> throw IllegalArgumentException("Unsupported type: $type")
        }
    }

    private fun add(left: Any, right: Any): Any {
        return when {
            left is Number && right is Number -> left.toDouble() + right.toDouble()
            left is String && right is String -> left + right
            else -> throw IllegalArgumentException("Cannot add $left and $right")
        }
    }

    private fun subtract(left: Any, right: Any): Any {
        if (left is Number && right is Number) {
            return left.toDouble() - right.toDouble()
        }
        throw IllegalArgumentException("Cannot subtract $left and $right")
    }

    private fun multiply(left: Any, right: Any): Any {
        return when {
            left is Number && right is Number -> left.toDouble() * right.toDouble()
            left is String && right is Number -> left.repeat(right.toInt())
            right is String && left is Number -> right.repeat(left.toInt())
            else -> throw IllegalArgumentException("Cannot multiply $left and $right")
        }
    }

    private fun divide(left: Any, right: Any): Any {
        if (left is Number && right is Number) {
            if (right.toDouble() == 0.0) throw IllegalArgumentException("Division by zero")
            return left.toDouble() / right.toDouble()
        }
        throw IllegalArgumentException("Cannot divide $left and $right")
    }
}