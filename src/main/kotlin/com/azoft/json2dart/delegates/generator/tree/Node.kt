package com.azoft.json2dart.delegates.generator.tree

//sealed class Node (val name: String, val parent: Node?, val depth: Int = 0)
sealed class Node (val name: String, val depth: Int = 0)

//class ClassNode(name: String, val childs: List<Node>, parent: Node? = null): Node(name, parent)
class ClassNode(name: String, val childs: List<Node>): Node(name)

//class NullNode(name: String, parent: Node? = null): Node(name, parent)
class NullNode(name: String, parent: Node? = null): Node(name)


//sealed class ValueNode<T>(name: String, val value: T, parent: Node?): Node(name, parent) {
sealed class ValueNode<T>(name: String, val value: T): Node(name) {
    protected abstract var typeSalt: Int

    override fun hashCode(): Int {
        return 31 * name.hashCode() + typeSalt
    }
}

//class StringNode(name: String, value: String, parent: Node? = null): ValueNode<String>(name, value, parent) {
class StringNode(name: String, value: String): ValueNode<String>(name, value) {
    override var typeSalt: Int = 3
}

//class DoubleNode(name: String, value: Double, parent: Node? = null): ValueNode<Double>(name, value, parent) {
class DoubleNode(name: String, value: Double): ValueNode<Double>(name, value) {
    override var typeSalt: Int = 5
}

//class IntNode(name: String, value: Int, parent: Node? = null): ValueNode<Int>(name, value, parent) {
class IntNode(name: String, value: Int): ValueNode<Int>(name, value) {
    override var typeSalt: Int = 7
}

//class BooleanNode(name: String, value: Boolean, parent: Node? = null): ValueNode<Boolean>(name, value, parent) {
class BooleanNode(name: String, value: Boolean): ValueNode<Boolean>(name, value) {
    override var typeSalt: Int = 11
}

//class ListNode(name: String, value: Node, parent: Node? = null): ValueNode<Node>(name, value, parent) {
class ListNode(name: String, value: Node): ValueNode<Node>(name, value) {
    override var typeSalt: Int = 13
}
