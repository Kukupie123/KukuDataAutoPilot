package dev.kukukodes.kdap.actionservice

import dev.kukukodes.kdap.actionservice.models.actions.ActionConnection
import dev.kukukodes.kdap.actionservice.models.actions.ActionRunnerEngine
import dev.kukukodes.kdap.actionservice.models.actions.InnerAction
import dev.kukukodes.kdap.actionservice.models.actions.UserAction
import dev.kukukodes.kdap.actionservice.models.actions.definedActions.AddAction
import dev.kukukodes.kdap.actionservice.models.actions.definedActions.MultiplyAction
import dev.kukukodes.kdap.actionservice.models.actions.plug.ActionPlug
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

class ActionServiceTest {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Test
    fun version2() {
        val storage = mutableMapOf(
            "one" to 1,
            "two" to 2,
            "three" to 3,
        )

        val add = AddAction()
        val addConnection = ActionConnection(
            plugInMap = mapOf(
                "num1" to "one",
                "num2" to "two",
            ),
            plugOutMap = mapOf(
                "result" to "sum"
            )
        )

        val pro = MultiplyAction()
        val proConnection = ActionConnection(
            plugInMap = mapOf(
                "num1" to "sum",
                "num2" to "three"
            ),
            plugOutMap = mapOf(
                "result" to "final"
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
            outputMap = mapOf("result" to "final")
        )

        val engine = ActionRunnerEngine()
        val finalOutput = engine.executeAction(userAddMulti, storage)

        println("Final output: $finalOutput")
    }
}
