package com.azoft.json2dart.delegates.generator.tree

import com.fasterxml.jackson.databind.JsonNode


class JsonNodeConverter(
    private val collisionResolver: AbstractCollisionResolver = AutomaticCollisionResolver()
) {
    fun extractNodes(rootName: String, rootJsonNode: JsonNode, squash: Boolean = false): Node {

        val nameMap = mutableMapOf<String, ClassNode>()
        return rootJsonNode.convertNode(
            name = rootName,
            corrector = { newNode ->
                if (!nameMap.contains(newNode.name)) {
                    nameMap[newNode.name] = newNode
                    newNode
                } else {
                    val oldNode = nameMap.remove(newNode.name)
                        ?: return@convertNode newNode
                    val (oldResolvedName, newResolvedName) = collisionResolver.resolve(oldNode, newNode)

                    oldNode.name = oldResolvedName
                    newNode.name = newResolvedName
                    nameMap[oldResolvedName] = oldNode
                    nameMap[newResolvedName] = newNode
                    newNode
                }
            }
        )
    }

    private fun JsonNode.convertNode(
        name: String,
        parent: Node? = null,
        corrector: (ClassNode) -> ClassNode
    ): Node =
            when {
                isDouble || isFloat || isBigDecimal ->
                    DoubleNode(name, doubleValue(), parent)

                isShort || isInt || isLong || isBigInteger ->
                    IntNode(name, intValue(), parent)

                isBoolean -> BooleanNode(name, booleanValue(), parent)

                isTextual -> StringNode(name, textValue(), parent)

                isArray -> ListNode(
                    name,
                    elementAtOrNull(0)?.convertNode(name, parent, corrector) ?: NullNode(name),
                    parent
                )

                isObject -> corrector(
                    ClassNode(name = name, parent = parent).apply classNode@{
                        childs = fields().asSequence().map { (fieldName, field) ->
                            field.convertNode(fieldName, this@classNode, corrector)
                        }.toList()
                    }
                )

                else -> NullNode(name)
            }
}
