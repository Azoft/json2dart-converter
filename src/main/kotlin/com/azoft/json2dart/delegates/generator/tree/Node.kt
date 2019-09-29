package com.azoft.json2dart.delegates.generator.tree

sealed class Node (
    var className: String,
    val parent: Node?,
    var depth: Int = 0
) {

    var fieldName: String? = null
    
    override fun hashCode(): Int {
        return className.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Node) return false

        if (className != other.className) return false
        if (depth != other.depth) return false

        return true
    }
}

class ClassNode(
    name: String,
    var childs: List<Node> = listOf(),
    parent: Node? = null
): Node(name, parent) {
    
    override fun hashCode(): Int {
        return 31 * childs.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ClassNode) return false

        if (childs != other.childs) return false

        return true
    }
}


sealed class ValueNode<T>(name: String, val value: T, parent: Node?)
    : Node(name, parent) {

    protected abstract var typeSalt: Int
    
    override fun hashCode(): Int {
        return 31 * super.hashCode() + typeSalt
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ValueNode<*>) return false
        if (!super.equals(other)) return false

        return true
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