package com.azoft.json2dart.delegates.generator.tree

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import java.util.*

private typealias NamedObjectNode = Pair<String, ObjectNode>
private typealias NamedJsonNode = Pair<String, JsonNode>
data class LinkedJsonNode(
    val name: String,
    val node: JsonNode,
    val depth: Int
)


class NodeTree {

    private val nodeMap = hashMapOf<Int, Node>()

    fun prepareTree(rootName: String, rootNode: JsonNode) {

        rootNode as? ObjectNode ?: throw Exception("root node must be an object")

        val nodeQueue = LinkedList<NamedObjectNode>().apply {
            add(rootName to rootNode)
        }

        while (nodeQueue.isNotEmpty()) {
//            nodeQueue.first.let queueWhile@ { (className, node) ->
//                node.fields().asSequence()
//                    .mapNotNull { (fieldName, field) ->
//                        (field as? ObjectNode)
//                            ?.let { objectNode ->  NamedJsonNode(fieldName, objectNode) }
//                    }
//                    .firstOrNull()
//                    ?.let { (fieldName, fieldNode) ->
//                        nodeQueue.addFirst(fieldName to fieldNode as ObjectNode)
//                        return@queueWhile
//                    }
//
//                ClassNode(
//                    className,
////                    node.fieldNames().asSequence()
////                        .map { fieldName ->
////                            node[fieldName].toPrimitiveNode(fieldName)
////                        }.toList()
//                )
//            }
        }
    }

    private fun JsonNode.toPrimitiveNode(name: String) =
        when {
            isDouble || isFloat || isBigDecimal -> DoubleNode(name, doubleValue())

            isShort || isInt || isLong || isBigInteger -> IntNode(name, intValue())

            isBoolean -> BooleanNode(name, booleanValue())

            isTextual -> StringNode(name, textValue())

            isArray -> TODO()

            isObject -> throw Exception("Unexpected object node")

            else -> NullNode(name)
        }

    private fun readTree2(rootName: String, rootNode: JsonNode) {

    }
}