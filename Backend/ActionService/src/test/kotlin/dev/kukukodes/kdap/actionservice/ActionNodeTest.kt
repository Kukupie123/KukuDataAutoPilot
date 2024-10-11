package dev.kukukodes.kdap.actionservice

import dev.kukukodes.kdap.actionservice.models.actions.ActionConnection
import dev.kukukodes.kdap.actionservice.models.actions.ActionRunnerEngine
import dev.kukukodes.kdap.actionservice.models.actions.InnerAction
import dev.kukukodes.kdap.actionservice.models.actions.UserAction
import dev.kukukodes.kdap.actionservice.models.actions.definedActions.AddAction
import dev.kukukodes.kdap.actionservice.models.actions.definedActions.MultiplyAction
import dev.kukukodes.kdap.actionservice.models.actions.plug.ActionPlug
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

class ActionServiceTest {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Test
    fun squareAction() {
        val storage = mutableMapOf(
            "storageNum" to 2
        )
        val userSquareAction = createSquareAction()
        val engine = ActionRunnerEngine()
        val result = engine.executeAction(
            InnerAction(
                userSquareAction,
                // Pass initial connection.
                // The 'number' input of squareAction needs to get its value from storage's storageNum key
                ActionConnection(
                    plugInMap = mapOf(
                        "number" to "{storageNum}"
                    ),
                )
            ),
            storage = storage
        )
        log.info(result.toString())
        Assertions.assertThat(result).isNotNull
        // The expected result is the square of 2, i.e., 4.0
        Assertions.assertThat(result?.get("resultSQUARE")).isEqualTo(4.0f)
    }

    @Test
    fun nestedPlugs() {
        // Initialize storage with nested maps
        val storage = mutableMapOf(
            "add" to mutableMapOf(
                "num1" to 2,  // First number for the add action
                "num2" to 5   // Second number for the add action
            ),
            "product" to 2   // Number for the multiplication action
        )

        // Create a UserAction that adds two numbers and then multiplies them by a third
        val addMultiAction = createAddAndMultiplyAction()
        val engine = ActionRunnerEngine()
        val result = engine.executeAction(
            InnerAction(
                addMultiAction,
                // Map storage values to the action's plug-ins using the plug-in map
                ActionConnection(
                    plugInMap = mapOf(
                        "number1" to "{add.num1}",  // Use num1 from add map in storage
                        "number2" to "{add.num2}",  // Use num2 from add map in storage
                        "number3" to "{product}"    // Use product from storage
                    )
                )
            ), storage
        )
        log.info(result.toString())
        // Assertions can be added here based on what the expected result is
    }

    @Test
    fun compositeAction() {
        // Initialize storage with a single value
        val storage = mapOf(
            "storageNum" to 2
        )
        // Create a composite action that first squares a number, then performs addition and multiplication
        val compositeAction = createCompositeAction()
        val engine = ActionRunnerEngine()
        val result = engine.executeAction(
            InnerAction(
                compositeAction,
                // Pass the number from storage as input for the composite action
                ActionConnection(
                    plugInMap = mapOf(
                        "num" to "{storageNum}"
                    )
                )
            ), storage
        )
        log.info(result.toString())
        // Assertions can be added here based on what the expected result is
    }

    private fun createCompositeAction(): UserAction {
        // This action will first square a number and then use the result in an add-and-multiply action
        val square = createSquareAction()
        val addMulti = createAddAndMultiplyAction()
        return UserAction(
            name = "Composite",
            plugIn = mapOf(
                // num is the input for the composite action
                "num" to ActionPlug.Primitive("DECIMAL", defaultValue = 0)
            ),
            plugOut = mapOf(
                // result will be the final output of the composite action
                "result" to ActionPlug.Primitive("DECIMAL", defaultValue = 0)
            ),
            actions = listOf(
                // Square action
                InnerAction(
                    action = square,
                    // Pass num to squareAction, and store the result in result.square
                    actionConnection = ActionConnection(
                        plugInMap = mapOf(
                            "number" to "{num}"
                        ),
                        plugOutMap = mapOf(
                            "{resultSQUARE}" to "{result.square}"
                        )
                    )
                ),
                // Add and multiply action
                InnerAction(
                    action = addMulti,
                    // Use the result of the square action for addMulti inputs
                    actionConnection = ActionConnection(
                        plugInMap = mapOf(
                            "number1" to "{result.square}",
                            "number2" to "{result.square}",
                            "number3" to "{result.square}"
                        ),
                        plugOutMap = mapOf(
                            "{result}" to "{result.addMulti}"
                        )
                    )
                )
            ),
            // The output of the composite action will be the result from the addMulti action
            outputMap = mapOf(
                "result" to "{result.addMulti}"
            )
        )
    }

    private fun createAddAndMultiplyAction(): UserAction {
        // Create a user-defined action that first adds two numbers, then multiplies the result by a third
        return UserAction(
            name = "Add Multiply Action",
            description = "Adds two numbers, then multiplies the result by a third number",
            plugIn = mapOf(
                "number1" to ActionPlug.Primitive("DECIMAL", defaultValue = 0), // First number to add
                "number2" to ActionPlug.Primitive("DECIMAL", defaultValue = 0), // Second number to add
                "number3" to ActionPlug.Primitive("DECIMAL", defaultValue = 0), // Number to multiply the sum by
            ),
            plugOut = mapOf(
                // Final result will be stored here
                "result" to ActionPlug.Primitive("DECIMAL", defaultValue = 0)
            ),
            actions = listOf(
                // Add action
                InnerAction(
                    action = AddAction(),
                    // Pass the inputs number1 and number2 to AddAction, store the result in result.add
                    actionConnection = ActionConnection(
                        plugInMap = mapOf(
                            "num1" to "{number1}",
                            "num2" to "{number2}",
                        ),
                        plugOutMap = mapOf(
                            "{result}" to "{result.add}"
                        )
                    )
                ),
                // Multiply action
                InnerAction(
                    action = MultiplyAction(),
                    // Use the result of AddAction for num1, and number3 for num2 in MultiplyAction
                    actionConnection = ActionConnection(
                        plugInMap = mapOf(
                            "num1" to "{result.add}",
                            "num2" to "{number3}"
                        ),
                        plugOutMap = mapOf(
                            "{result}" to "{result.multiply}"
                        )
                    )
                )
            ),
            // The output of the add and multiply action will be the result of MultiplyAction
            outputMap = mapOf(
                "result" to "{result.multiply}"
            )
        )
    }

    private fun createSquareAction(): UserAction {
        // Create an action that squares a number by multiplying it by itself
        return UserAction(
            name = "Square action",
            description = "Squares a number",
            // number is the input, which is a decimal
            plugIn = mapOf(
                "number" to ActionPlug.Primitive("DECIMAL", defaultValue = 0)
            ),
            // resultSQUARE is the output, which will store the squared result
            plugOut = mapOf(
                "resultSQUARE" to ActionPlug.Primitive(type = "DECIMAL", defaultValue = 0),
            ),
            actions = listOf(
                // Multiply action (squares the number by multiplying it by itself)
                InnerAction(
                    action = MultiplyAction(),
                    actionConnection = ActionConnection(
                        // Pass the input number to both num1 and num2 for multiplication
                        plugInMap = mapOf(
                            "num1" to "{number}",
                            "num2" to "{number}",
                        ),
                        // Store the result in result.square
                        plugOutMap = mapOf(
                            "{result}" to "{result.square}"
                        ),
                    )
                )
            ),
            // The output will map resultSQUARE to result.square
            outputMap = mapOf(
                "resultSQUARE" to "{result.square}"
            )
        )
    }
}
