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

    private fun JsonNode.convertNode(name: String, parent: Node? = null, corrector: (Node) -> Node): Node =
        when {
            isDouble || isFloat || isBigDecimal -> DoubleNode(name, doubleValue(), parent)

            isShort || isInt || isLong || isBigInteger -> IntNode(name, intValue(), parent)

            isBoolean -> BooleanNode(name, booleanValue(), parent)

            isTextual -> StringNode(name, textValue(), parent)

            isArray -> ListNode(
                name,
                elementAtOrNull(0)?.convertNode(name, parent, corrector) ?: NullNode(name),
                parent
            )

            isObject -> corrector(
                ClassNode(name = name, parent = parent).apply classNode@ {
                    childs = fields().asSequence().map { (fieldName, field) ->
                        field.convertNode(fieldName, this@classNode, corrector)
                    }.toList()
                }
            )

            else -> NullNode(name)
        }
}
