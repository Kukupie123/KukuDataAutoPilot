package dev.kukukodes.kdap.actionservice.models.action.systemactions

import dev.kukukodes.kdap.actionservice.models.action.SystemAction

class SumAction : SystemAction("Sum") {
    override fun getInputStructure(): Map<String, Any> {
        return mapOf(
            "num1" to "INTEGER",
            "num2" to "INTEGER",
        )
    }

    override fun getOutputStructure(): Map<String, Any> {
        return mapOf(
            "sum" to "INTEGER",
        )
    }

    override fun execute(runtimeStorage: HashMap<String, Any>, inputValues: Map<String, Any>, outputName: String) {
        val num1Map = inputValues["num1"] as String
        val num2Map = inputValues["num2"] as String
        var num1 = -1
        if (num1Map.startsWith("{") && num1Map.endsWith("}")) {
            val variableLocationsNest = num1Map.split(".")
            if (variableLocationsNest.size == 1) {
                num1 = runtimeStorage[variableLocationsNest[0]].toString().toInt()
            } else {
                var map: Map<String, Any> = runtimeStorage
                for (i in 0 until variableLocationsNest.size - 1) {
                    map = map[variableLocationsNest[i]] as Map<String, Any>
                }
                num1 = map[variableLocationsNest[variableLocationsNest.size - 1]] as Int
            }
        } else {
            num1 = inputValues["num1"] as Int
        }
    }


}