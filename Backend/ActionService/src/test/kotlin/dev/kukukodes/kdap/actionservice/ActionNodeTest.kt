package dev.kukukodes.kdap.actionservice

import dev.kukukodes.kdap.actionservice.models.actions.ActionConnection
import dev.kukukodes.kdap.actionservice.models.actions.InnerAction
import dev.kukukodes.kdap.actionservice.models.actions.UserAction
import dev.kukukodes.kdap.actionservice.models.actions.plug.ActionPlug
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

class ActionServiceTest {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Test
    fun version2() {

        val storage = mutableMapOf<String, Any?>(
            "one" to 1,
            "two" to 2,
            "three" to 3,
        )
        /*

         */
        val add = dev.kukukodes.kdap.actionservice.models.actions.definedActions.AddAction()
        val addConnection = ActionConnection(
            plugInMap = mapOf(
                "num1" to "one",//storage[add][one] (Nested value)
                "num2" to "two",
            ),
            plugOutMap = mapOf(
                "result" to "sum" // Will be stored in storage[sum]
            )
        )
        val pro = dev.kukukodes.kdap.actionservice.models.actions.definedActions.MultiplyAction()
        val proConnection = ActionConnection(
            plugInMap = mapOf(
                "num1" to "sum", //storage[sum]
                "num2" to "three" //storage[multiply]
            ),
            plugOutMap = mapOf(
                "result" to "final" //Will be stored in storage[final]
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

        val finalStorage = userAddMulti.execute(storage);

        print("GGEZ")
    }
}
