package com.azoft.json2dart.delegates.generator.tree

import kotlin.collections.Map

sealed class Node (
    val name: String,
    val parent: VirtualAddress?,
    val depth: Int = 0
) {

    protected var cachedHashcode: Int? = null
        get() = field ?: hashCode().also { cachedHashcode = it }
        set(value) {
            if (value != null) {
                return
            }
            field = value
        }

    val virtualAddress by lazy {
        VirtualAddress(cachedHashcode ?: hashCode())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ClassNode) return false

        return other.cachedHashcode == cachedHashcode
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}

class ClassNode(
    name: String,
//    childs: List<Node> = listOf(),
    childs: List<VirtualAddress> = listOf(),
    parent: VirtualAddress? = null
): Node(name, parent) {

    var childs: List<VirtualAddress> = childs
    set(value) {
        field = value
        cachedHashcode = null
    }

    override fun hashCode(): Int {
        return 31 * childs.hashCode() + name.hashCode()
    }
}


sealed class ValueNode<T>(name: String, val value: T, parent: VirtualAddress?): Node(name, parent) {
    protected abstract var typeSalt: Int

    override fun hashCode(): Int {
        return 31 * super.hashCode() + typeSalt
    }
}

class StringNode(name: String, value: String, parent: VirtualAddress? = null): ValueNode<String>(name, value, parent) {
    override var typeSalt: Int = 3
}

class DoubleNode(name: String, value: Double, parent: VirtualAddress? = null): ValueNode<Double>(name, value, parent) {
    override var typeSalt: Int = 5
}

class IntNode(name: String, value: Int, parent: VirtualAddress? = null): ValueNode<Int>(name, value, parent) {
    override var typeSalt: Int = 7
}

class BooleanNode(name: String, value: Boolean, parent: VirtualAddress? = null): ValueNode<Boolean>(name, value, parent) {
    override var typeSalt: Int = 11
}

class ListNode(name: String, value: Node, parent: VirtualAddress? = null): ValueNode<Node>(name, value, parent) {
    override var typeSalt: Int = 13
}

class NullNode(name: String, parent: VirtualAddress? = null): ValueNode<Unit>(name, Unit, parent) {
    override var typeSalt: Int = 17
}

fun ClassNode.copy(
    name: String = this.name,
    childs: List<VirtualAddress> = this.childs,
    parent: VirtualAddress? = this.parent
) : ClassNode =
    ClassNode(name = name, parent = parent, childs = childs)

fun <T> ValueNode<T>.copy(
    name: String = this.name,
    value: T = this.value,
    parent: VirtualAddress? = this.parent
): ValueNode<*> {
    return when(this) {
        is StringNode -> StringNode(name, value as String, parent)
        is DoubleNode -> DoubleNode(name, value as Double, parent)
        is IntNode -> IntNode(name, value as Int, parent)
        is BooleanNode -> BooleanNode(name, value as Boolean, parent)
        is ListNode -> ListNode(name, value as Node, parent)
        is NullNode -> NullNode(name, parent)
    }
}

inline class VirtualAddress(val address: Int)

//inline class VirtualMemory(private val space: Map<VirtualAddress, Node>) {
inline class VirtualMemory(val space: Map<VirtualAddress, Node>) {
    public operator fun get(key: VirtualAddress): Node? = space[key]
}