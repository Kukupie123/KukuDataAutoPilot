package dev.kukukodes.kdap.actionservice.models.action

/**
 * Sealed interface representing the structure of action inputs and outputs.
 * It allows for primitive types and nested maps as its subtypes.
 *
 * Subtypes:
 * - Primitive types: TEXT, INTEGER, DECIMAL, BOOLEAN
 * - Complex type: NestedMap, which can contain a map of any ActionInputOutputStructure
 */
sealed interface ActionInputOutputStructure {
    data object TEXT : ActionInputOutputStructure
    data object INTEGER : ActionInputOutputStructure
    data object DECIMAL : ActionInputOutputStructure
    data object BOOLEAN : ActionInputOutputStructure
    class NestedMap(val value: Map<String, ActionInputOutputStructure>) : ActionInputOutputStructure
    data object ALL : ActionInputOutputStructure;
}