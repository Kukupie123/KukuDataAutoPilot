package dev.kukukodes.kdap.actionservice.models.actions.definedActions

import dev.kukukodes.kdap.actionservice.models.actions.plug.ActionPlug
import org.slf4j.LoggerFactory

class AddAction(

    override val name: String = "AddAction",
    override val description: String = "Action that adds two decimals",
    override val plugIn: Map<String, ActionPlug> = mapOf(
        "num1" to ActionPlug.Primitive("DECIMAL", defaultValue = 0),
        "num2" to ActionPlug.Primitive("DECIMAL", defaultValue = 0),
    ),
    override val plugOut: Map<String, ActionPlug> = mapOf(
        "result" to (ActionPlug.Primitive("DECIMAL", defaultValue = 0)

                ),
    ),
) : DefinedActionBase(name, description, plugIn, plugOut) {
    private val log = LoggerFactory.getLogger(AddAction::class.java)
    override fun execute(input: Map<String, Any?>): Map<String, Any?> {
        log.info("Adding $input")
        return mapOf("result" to (input["num1"].toString().toFloat() + input["num2"].toString().toFloat()))
    }
}