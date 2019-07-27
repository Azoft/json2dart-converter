package com.azoft.json2dart.delegates.generator.tree

import com.fasterxml.jackson.databind.JsonNode


class JsonNodeConverter(
    private val collisionResolver: AbstractCollisionResolver = AutomaticCollisionResolver()
) {

    val classNodeMap = mutableMapOf<String, VirtualAddress>()
    val memoryMap = HashMap<VirtualAddress, Node>()
    val memory = VirtualMemory(memoryMap as Map<VirtualAddress, Node>)

//    fun extractNodes(rootName: String, rootNode: JsonNode, squash: Boolean = false): List<Node> {
    fun extractNodes(rootName: String, rootNode: JsonNode, squash: Boolean = false): Pair<VirtualAddress, VirtualMemory> {
        /*
        * nodeMap -- для хранения нод и доступа к ним по "виртуальному адресу".
        * Туда попадают вообще все ноды и только здесь к ним сохраняется доступ
        *
        * classNodeMap -- мапа для проверки имен.
        * Позволяет проверять уникальность имен для класса
        *
        * memory -- переменная, которую мы будем подавать, когда нам где-то понадобится память
        * */
        val rootNodeAddress = rootNode.convertNode(
            name = rootName,
            save = { memoryMap.putAndReturn(it.virtualAddress, it) },
            corrector = { newNode ->
                correctNode(
                    newNode,
                    getFromMap = { classNodeMap[it] },
                    saveToMap = { classNodeMap[it.name] = it.virtualAddress },
                    removeFromMap = { classNodeMap.remove(it) },
                    getFromMemory = { memoryMap[it] },
                    saveToMemory = { memoryMap[it.virtualAddress] = it },
                    removeFromMemory = { memoryMap.remove(it) }
                )
            }
        ).virtualAddress
//        return classNodeMap.values.map { memoryMap[it]!! }.toList()
        return rootNodeAddress to memory
    }

    private fun JsonNode.convertNode(
        name: String,
        parent: Node? = null,
        corrector: (ClassNode) -> ClassNode,
        save: (Node) -> Node
    ): Node =
        save(
            when {
                isDouble || isFloat || isBigDecimal ->
                    DoubleNode(name, doubleValue(), parent?.virtualAddress)

                isShort || isInt || isLong || isBigInteger ->
                    IntNode(name, intValue(), parent?.virtualAddress)

                isBoolean -> BooleanNode(name, booleanValue(), parent?.virtualAddress)

                isTextual -> StringNode(name, textValue(), parent?.virtualAddress)

                isArray -> ListNode(
                    name,
                    elementAtOrNull(0)?.convertNode(name, parent, corrector, save) ?: NullNode(name),
                    parent?.virtualAddress
                )

                isObject -> corrector(
                    ClassNode(name = name, parent = parent?.virtualAddress).apply classNode@ {
                        childs = fields().asSequence().map { (fieldName, field) ->
                            field.convertNode(fieldName, this@classNode, corrector, save).virtualAddress
                        }.toList()
                    }
                )

                else -> NullNode(name)
            }
        )

    private fun correctNode(
        newNode: ClassNode,
        getFromMap: (name: String) -> VirtualAddress?,
        saveToMap: (node: ClassNode) -> Unit,
        removeFromMap: (name: String) -> Unit,
        getFromMemory: (address: VirtualAddress) -> Node?,
        saveToMemory: (node: Node) -> Unit,
        removeFromMemory: (address: VirtualAddress) -> Unit
    ): ClassNode {
        val oldNodeAddress = getFromMap(newNode.name)
        if (oldNodeAddress == null) {
            saveToMap(newNode)
            return newNode
        }
        val oldNode = getFromMemory(oldNodeAddress) ?: throw NoSuchElementException("Cannot find old node in memory")
        (oldNode as? ClassNode) ?: throw TypeCastException("Found old node is not a class node")

        val (resolvedOld, resolvedNew) = collisionResolver.resolve(oldNode, newNode)

        removeFromMap(oldNode.name)
        removeFromMemory(oldNodeAddress)

        saveToMemory(resolvedOld)
        saveToMap(resolvedOld)
        saveToMap(resolvedNew)

        return resolvedNew
    }

    private fun <K, V> MutableMap<K, V>.putAndReturn(key: K, value: V): V {
        put(key, value)
        return value
    }
}
