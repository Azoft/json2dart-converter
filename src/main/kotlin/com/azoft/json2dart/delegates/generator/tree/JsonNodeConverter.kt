package com.azoft.json2dart.delegates.generator.tree

import com.azoft.json2dart.delegates.ui.IntellijUIDelegate
import com.azoft.json2dart.delegates.ui.UIDelegate
import com.fasterxml.jackson.databind.JsonNode


class JsonNodeConverter(
    private val collisionResolver: AbstractCollisionResolver = AutomaticCollisionResolver()
) {


    fun extractNodes(rootName: String, rootNode: JsonNode, squash: Boolean = false): List<Node> {
        val classNodeMap = mutableMapOf<String, ClassNode>()
        rootNode.convertNode(rootName) { newNode ->
            val oldNode = classNodeMap[newNode.name]
                ?: return@convertNode newNode.apply { classNodeMap[name] = this }

            val oldName: String? = oldNode.name
            val (resolvedOld, resolvedNew) = collisionResolver.resove(oldNode, newNode)

            if (oldName != resolvedOld.name) {
                classNodeMap[resolvedOld.name] = resolvedOld
            }
            classNodeMap[resolvedNew.name] = resolvedNew

            return@convertNode resolvedNew
        }
        return classNodeMap.values.toList()
    }

    private fun JsonNode.convertNode(name: String, parent: Node? = null, corrector: (ClassNode) -> ClassNode): Node =
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
