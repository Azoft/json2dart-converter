package com.azoft.json2dart.delegates.generator.tree

sealed class Node (val name: String, val parent: Node?, val depth: Int = 0) {

    protected var cachedHashcode: Int? = null
        get() = field ?: hashCode().also { cachedHashcode = it }

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
    childs: List<Node> = listOf(),
    parent: Node? = null
): Node(name, parent) {

    var childs: List<Node> = childs
    set(value) {
        field = value
        cachedHashcode = null
    }

    override fun hashCode(): Int {
        return 31 * childs.hashCode() + name.hashCode()
    }
}


sealed class ValueNode<T>(name: String, val value: T, parent: Node?): Node(name, parent) {
    protected abstract var typeSalt: Int

    override fun hashCode(): Int {
        return 31 * super.hashCode() + typeSalt
    }
}

class StringNode(name: String, value: String, parent: Node? = null): ValueNode<String>(name, value, parent) {
    override var typeSalt: Int = 3
}

class DoubleNode(name: String, value: Double, parent: Node? = null): ValueNode<Double>(name, value, parent) {
    override var typeSalt: Int = 5
}

class IntNode(name: String, value: Int, parent: Node? = null): ValueNode<Int>(name, value, parent) {
    override var typeSalt: Int = 7
}

class BooleanNode(name: String, value: Boolean, parent: Node? = null): ValueNode<Boolean>(name, value, parent) {
    override var typeSalt: Int = 11
}

class ListNode(name: String, value: Node, parent: Node? = null): ValueNode<Node>(name, value, parent) {
    override var typeSalt: Int = 13
}

class NullNode(name: String, parent: Node? = null): ValueNode<Unit>(name, Unit, parent) {
    override var typeSalt: Int = 17
}

fun ClassNode.copy(
    name: String = this.name,
    childs: List<Node> = this.childs,
    parent: Node? = this.parent
) : ClassNode =
    ClassNode(name = name, parent = parent).apply {
        this.childs = childs.map {
            when(it) {
                is ClassNode -> it.copy(parent = this)
                is ValueNode<*> -> it.copy(parent = this)
            }
        }
    }

fun <T> ValueNode<T>.copy(
    name: String = this.name,
    value: T = this.value,
    parent: Node? = this.parent
): ValueNode<*> = when(this) {
    is StringNode -> StringNode(name, value as String, parent)
    is DoubleNode -> DoubleNode(name, value as Double, parent)
    is IntNode -> IntNode(name, value as Int, parent)
    is BooleanNode -> BooleanNode(name, value as Boolean, parent)
    is ListNode -> ListNode(name, value as Node, parent)
    is NullNode -> NullNode(name, parent)
}