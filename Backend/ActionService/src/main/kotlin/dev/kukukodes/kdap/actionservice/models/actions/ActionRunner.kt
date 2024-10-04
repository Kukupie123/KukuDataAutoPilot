package dev.kukukodes.kdap.actionservice.models.actions

class ActionRunner(private val action: Action, private val storage: MutableMap<String, Any?>) {
    fun run (){
        action.execute()
    }
}