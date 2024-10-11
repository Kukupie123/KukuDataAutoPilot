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
    fun version2() {
        val storage = mutableMapOf(
            "add" to mutableMapOf(
                "one" to 5,
                "two" to "10"
            ),
            "product" to 3,
        )

        val add = AddAction()
        val addConnection = ActionConnection(
            plugInMap = mapOf(
                "num1" to "{add.one}",
                "num2" to "{add.two}",
            ),
            plugOutMap = mapOf(
                "{result}" to "{sum}"
            )
        )

        val pro = MultiplyAction()
        val proConnection = ActionConnection(
            plugInMap = mapOf(
                "num1" to "{sum}",
                "num2" to "{product}"
            ), plugOutMap = mapOf(
                "{result}" to "{final}"
            )
        )

        val userAddMulti = UserAction(
            "Add and multiplication",
            "Adds two number then multiplies the result with another number",
            plugIn = mapOf(
                "num" to ActionPlug.Primitive("DECIMAL", defaultValue = 1)
            ),
            plugOut = mapOf(
                "result" to ActionPlug.Primitive(type = "DECIMAL", defaultValue = 0),
            ),
            actions = listOf(InnerAction(add, addConnection), InnerAction(pro, proConnection)),
            outputMap = mapOf("{result}" to "{final}")
        )

        val engine = ActionRunnerEngine()
        val finalOutput = engine.executeAction(userAddMulti, storage)

        println("Final output: $finalOutput")
        Assertions.assertThat(finalOutput).isNotNull
        Assertions.assertThat(finalOutput?.containsKey("result")).isTrue()
        Assertions.assertThat(finalOutput?.get("result")).isEqualTo(45.0f)
    }

    @Test
    fun nestedInputAndOutputTest() {
        val storage = mutableMapOf(
            "add" to mutableMapOf(
                "one" to 5,
                "two" to "10"
            ),
            "product" to 3,
        )

        val add = AddAction()
        val addConnection = ActionConnection(
            plugInMap = mapOf(
                "num1" to "{add.one}",
                "num2" to "{add.two}",
            ),
            //Store the value of result in sum
            plugOutMap = mapOf(
                "{result}" to "{result.sum}"
            )
        )

        val pro = MultiplyAction()
        val proConnection = ActionConnection(
            plugInMap = mapOf(
                "num1" to "{result.sum}",
                "num2" to "{product}"
            ), plugOutMap = mapOf(
                "{result}" to "{result.final}"
            )
        )

        val userAddMulti = UserAction(
            "Add and multiplication",
            "Adds two number then multiplies the result with another number",
            plugIn = mapOf(
                "num" to ActionPlug.Primitive("DECIMAL", defaultValue = 1)
            ),
            plugOut = mapOf(
                "result" to ActionPlug.Primitive(type = "DECIMAL", defaultValue = 0),
            ),
            actions = listOf(InnerAction(add, addConnection), InnerAction(pro, proConnection)),
            outputMap = mapOf("result" to "{result.final}")
        )

        val engine = ActionRunnerEngine()
        val finalOutput = engine.executeAction(userAddMulti, storage)

        println("Final output: $finalOutput")
        Assertions.assertThat(finalOutput).isNotNull
        Assertions.assertThat(finalOutput?.containsKey("result")).isTrue()
        Assertions.assertThat(finalOutput?.get("result")).isEqualTo(45.0f)
    }

}
