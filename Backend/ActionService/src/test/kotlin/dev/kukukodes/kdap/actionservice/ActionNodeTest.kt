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
        Assertions.assertThat(result?.get("result")).isEqualTo(4.0f)
    }

    @Test
    fun nestedPlugs() {
        val storage = mutableMapOf(
            "add" to mutableMapOf(
                "num1" to 2,
                "num2" to 5,
            ),
            "product" to 2
        )

        val addMultiAction = createAddAndMultiplyAction()
        val engine = ActionRunnerEngine()
        val result = engine.executeAction(
            InnerAction(
                addMultiAction,
                ActionConnection(
                    plugInMap = mapOf(
                        "number1" to "{add.num1}",
                        "number2" to "{add.num2}",
                        "number3" to "{product}"
                    )
                )
            ), storage
        )
        log.info(result.toString())
    }

    @Test
    fun compositeAction() {
        val storage = mapOf(
            "storageNum" to 2
        )
        val compositeAction = createCompositeAction()
        val engine = ActionRunnerEngine()
        val result = engine.executeAction(
            InnerAction(
                compositeAction,
                ActionConnection(
                    plugInMap = mapOf(
                        "num" to "{storageNum}"
                    )
                )
            ), storage
        )
        log.info(result.toString())
    }

    private fun createCompositeAction(): UserAction {

        val square = createSquareAction()
        val addMulti = createAddAndMultiplyAction()
        return UserAction(
            name = "Composite",
            plugIn = mapOf(
                "num" to ActionPlug.Primitive("DECIMAL", defaultValue = 0)
            ),
            plugOut = mapOf(
                "result" to ActionPlug.Primitive("DECIMAL", defaultValue = 0)
            ),
            actions = listOf(
                //Square action
                InnerAction(
                    action = square,
                    actionConnection = ActionConnection(
                        plugInMap = mapOf(
                            "number" to "{num}"
                        ),
                        plugOutMap = mapOf(
                            "{result}" to "{result.square}"
                        )
                    )
                ),
                //addMulti action
                InnerAction(
                    action = addMulti,
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
            outputMap = mapOf(
                "result" to "{result.addMulti}"
            )
        )
    }


    private fun createAddAndMultiplyAction(): UserAction {
        return UserAction(
            name = "Add Multiply Action",
            description = "Adds a number then multiplies",
            plugIn = mapOf(
                "number1" to ActionPlug.Primitive("DECIMAL", defaultValue = 0),
                "number2" to ActionPlug.Primitive("DECIMAL", defaultValue = 0),
                "number3" to ActionPlug.Primitive("DECIMAL", defaultValue = 0),
            ),
            plugOut = mapOf(
                "result" to ActionPlug.Primitive("DECIMAL", defaultValue = 0),
            ),
            actions = listOf(
                //Add inner action
                InnerAction(
                    action = AddAction(),
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
                //Multiple inner action
                InnerAction(
                    action = MultiplyAction(),
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
            outputMap = mapOf(
                "result" to "{result.multiply}"
            )
        )
    }


    private fun createSquareAction(): UserAction {
        return UserAction(
            name = "Square action",
            description = "Square action",
            plugIn = mapOf(
                "number" to ActionPlug.Primitive("DECIMAL", defaultValue = 0)
            ),
            plugOut = mapOf(
                "result" to ActionPlug.Primitive(type = "DECIMAL", defaultValue = 0),
            ),
            actions = listOf(
                InnerAction(
                    action = MultiplyAction(),
                    actionConnection = ActionConnection(
                        plugInMap = mapOf(
                            "num1" to "{number}",
                            "num2" to "{number}",
                        ),
                        plugOutMap = mapOf(
                            "{result}" to "{result.square}"
                        ),
                    )
                )
            ),
            outputMap = mapOf(
                "result" to "{result.square}"
            )
        )
    }
}
