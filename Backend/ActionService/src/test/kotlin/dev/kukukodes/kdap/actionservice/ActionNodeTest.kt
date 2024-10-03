package dev.kukukodes.kdap.actionservice

import dev.kukukodes.kdap.actionservice.models.ActionDefinition
import dev.kukukodes.kdap.actionservice.models.AddAction
import dev.kukukodes.kdap.actionservice.models.MultiplyAction
import dev.kukukodes.kdap.actionservice.models.RuntimeAction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

class ActionServiceTest {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Test
    fun sumAndMultiplyTest() {
        // Simulated runtime storage
        val runtimeStorage = mutableMapOf<String, Any>(
            "first" to mapOf(
                "num1" to 123, "num2" to 456
            ),
            "nest" to mapOf(
                "third" to 53
            )
        )

        log.info("Starting sumAndMultiplyTest with runtimeStorage: {}", runtimeStorage)

        // Step 1: SumAction (adds num1 and num2)
        val runtimeSum = RuntimeAction(
            action = AddAction(),
            runtimeStorage = runtimeStorage,
            inputMap = mapOf(
                "Num1" to "{first.num1}",  // reference to runtimeStorage key
                "Num2" to "{first.num2}"
            ),
            saveName = "sumResult"
        )

        log.info("Configured RuntimeAction for Sum: input map: {}", runtimeSum.toString())

        // Step 2: MultiplyAction (multiplies sumResult and third)
        val runtimeMultiply = RuntimeAction(
            action = MultiplyAction(),
            runtimeStorage = runtimeStorage,
            inputMap = mapOf(
                "Num1" to "{sumResult.Result}",
                "Num2" to "{nest.third}"
            ),
            saveName = "multiResult"
        )

        log.info("Configured RuntimeAction for Multiply: input map: {}", runtimeMultiply.toString())

        // Action Definition: Sum + Multiply
        val sumAndMultiplyAction = ActionDefinition(
            id = "SumAndMultiply",
            inputStructure = mapOf(
                "first" to mapOf(
                    "num1" to "INTEGER",
                    "num2" to "INTEGER"
                ),
                "nest" to mapOf(
                    "third" to "INTEGER"
                )
            ),
            outputStructure = mapOf(
                "result" to "DECIMAL"
            ),
            execution = listOf(runtimeSum, runtimeMultiply)
        )

        log.info("ActionDefinition created: SumAndMultiply")

        val runtimeSumMulti = RuntimeAction(
            action = sumAndMultiplyAction,
            runtimeStorage = runtimeStorage,
            inputMap = mapOf(
                "first" to mapOf(
                    "num1" to "{first.num1}",
                    "num2" to "{first.num2}"
                ),
                "nest" to mapOf(
                    "third" to "{nest.third}"
                )
            ),
            saveName = "multiSumResult"
        )

        // Execute the action
        runtimeSumMulti.executeAction()
        log.info("Storage after execution = $runtimeStorage")

        // Assertion
        val expectedSumResult = 579 // 123 + 456 = 579 (should match the integer type)
        val expectedMultiplyResult = expectedSumResult * 53 // (579 * 53)

        // Extract the actual result from the multiResult map
        val actualMultiplyResult = (runtimeStorage["multiResult"] as Map<String, Any>)["Result"]

    // Check if the runtime storage has the correct multiResult
        assertEquals(expectedMultiplyResult.toFloat(), actualMultiplyResult)
        log.info("Final Result after multiplication: {}", actualMultiplyResult)
    }


}
