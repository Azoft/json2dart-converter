package com.azoft.json2dart.delegates.generator.tree

import com.fasterxml.jackson.databind.JsonNode


class JsonNodeConverter {


    fun extractNodes(rootName: String, rootNode: JsonNode): List<Node> {
        val classNodeMap = mutableMapOf<Int, Node>()
        rootNode.convertNode(rootName) {
            classNodeMap[it.hashCode()] = it
            it
        }
        return classNodeMap.values.toList()
    }

    private fun JsonNode.convertNode(name: String, corrector: (Node) -> Node): Node =
        when {
            isDouble || isFloat || isBigDecimal -> DoubleNode(name, doubleValue())

            isShort || isInt || isLong || isBigInteger -> IntNode(name, intValue())

            isBoolean -> BooleanNode(name, booleanValue())

            isTextual -> StringNode(name, textValue())

            isArray -> ListNode(
                name,
                elementAtOrNull(0)?.convertNode(name, corrector) ?: NullNode(name)
            )

            isObject -> corrector(ClassNode(
                name,
                fields().asSequence().map { (fieldName, field) ->
                    field.convertNode(fieldName, corrector)
                }.toList()
            ))

            else -> NullNode(name)
        }
}
