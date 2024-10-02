package dev.kukukodes.kdap.actionservice.models

import dev.kukukodes.kdap.actionservice.models.action.Action

/**
 * An environment that action and action nodes will have access to when they are "executing"
 */
class ActionRuntimeEnvironment(private val storage: HashMap<String, Any>, private val action: Action) {
    fun extractValidValueFromInputMap(
        inputStruct: Map<String, Any>,
        inputMap: Map<String, Any>,
        runtimeStorage: HashMap<String, Any>
    ): Map<String, Any> {
        val result = mutableMapOf<String, Any>()

        for ((fieldName, type) in inputStruct) {
            val inputMapVal = inputMap[fieldName]

            if (inputMapVal is String && inputMapVal.startsWith("{") && inputMapVal.endsWith("}")) {
                val nestedMapVal = inputMapVal.substring(1, inputMapVal.length - 1).split(".")
                var value: Any? = runtimeStorage

                for (element in nestedMapVal) {
                    value = (value as? Map<String, Any>)?.get(element)
                    if (value == null) {
                        throw IllegalArgumentException("Invalid path in runtime storage for $inputMapVal")
                    }
                }

                // If the value is nested, recursively process it
                if (type is Map<*, *>) {
                    if (value is Map<*, *>) {
                        result[fieldName] =
                            extractValidValueFromInputMap(
                                type as Map<String, Any>,
                                value as Map<String, Any>,
                                runtimeStorage
                            )
                    } else {
                        throw Exception("Invalid input map $fieldName for ${value as String}")
                    }
                } else {
                    // Convert the value to the required type based on inputStruct
                    when (type) {
                        "INTEGER" -> {
                            result[fieldName] =
                                (value as? Int) ?: throw IllegalArgumentException("Invalid INTEGER type")
                        }

                        "DECIMAL" -> {
                            result[fieldName] =
                                (value as? Double) ?: throw IllegalArgumentException("Invalid DECIMAL type")
                        }

                        "TEXT" -> {
                            result[fieldName] = value.toString()
                        }

                        "BOOLEAN" -> {
                            result[fieldName] =
                                (value as? Boolean) ?: throw IllegalArgumentException("Invalid BOOLEAN type")
                        }

                        else -> throw IllegalArgumentException("Unknown type $type")
                    }
                }
            } else if (inputMapVal != null) {
                // Direct literal value (not a runtime variable)
                result[fieldName] = inputMapVal
            } else {
                throw IllegalArgumentException("Missing value for $fieldName")
            }
        }

        return result
    }


    fun start() {
        action.execute(storage);
    }
}