package dev.kukukodes.kdap.actionservice.models

/**
 * AddAction: Adds two numbers
 */
class AddAction : ActionDefinition(
    id = "AddAction", inputStructure = mapOf(
        "Num1" to "DECIMAL", "Num2" to "DECIMAL"
    ), outputStructure = mapOf(
        "Result" to "DECIMAL"
    ), execution = emptyList() // No runtime actions needed for simple math
) {
    override fun execute(inputValue: Map<String, Any>): Map<String, Any> {
        val num1 = (inputValue["Num1"] as Number).toDouble()
        val num2 = (inputValue["Num2"] as Number).toDouble()
        val result = num1 + num2
        return mapOf("Result" to result.toFloat())
    }
}

/**
 * SubtractAction: Subtracts Num2 from Num1
 */
class SubtractAction : ActionDefinition(
    id = "SubtractAction", inputStructure = mapOf(
        "Num1" to "DECIMAL", "Num2" to "DECIMAL"
    ), outputStructure = mapOf(
        "Result" to "DECIMAL"
    ), execution = emptyList()
) {
    override fun execute(inputValue: Map<String, Any>): Map<String, Any> {
        val num1 = (inputValue["Num1"] as Number).toDouble()
        val num2 = (inputValue["Num2"] as Number).toDouble()
        val result = num1 - num2
        return mapOf("Result" to result.toFloat())
    }
}

/**
 * MultiplyAction: Multiplies two numbers
 */
class MultiplyAction : ActionDefinition(
    id = "MultiplyAction", inputStructure = mapOf(
        "Num1" to "DECIMAL", "Num2" to "DECIMAL"
    ), outputStructure = mapOf(
        "Result" to "DECIMAL"
    ), execution = emptyList()
) {
    override fun execute(inputValue: Map<String, Any>): Map<String, Any> {
        val num1 = (inputValue["Num1"] as Number).toDouble()
        val num2 = (inputValue["Num2"] as Number).toDouble()
        val result = num1 * num2
        return mapOf("Result" to result.toFloat())
    }
}

/**
 * DivideAction: Divides Num1 by Num2, handles division by zero
 */
class DivideAction : ActionDefinition(
    id = "DivideAction", inputStructure = mapOf(
        "Num1" to "DECIMAL", "Num2" to "DECIMAL"
    ), outputStructure = mapOf(
        "Result" to "DECIMAL"
    ), execution = emptyList()
) {
    override fun execute(inputValue: Map<String, Any>): Map<String, Any> {
        val num1 = (inputValue["Num1"] as Number).toDouble()
        val num2 = (inputValue["Num2"] as Number).toDouble()
        if (num2 == 0.0) throw IllegalArgumentException("Division by zero is not allowed")
        val result = num1 / num2
        return mapOf("Result" to result.toFloat())
    }
}
