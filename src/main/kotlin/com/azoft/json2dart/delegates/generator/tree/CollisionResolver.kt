package com.azoft.json2dart.delegates.generator.tree

import com.azoft.json2dart.delegates.ui.UIDelegate
import java.lang.StringBuilder
import java.util.*
import kotlin.test.todo

typealias ResolvedNodeNames = Pair<String, String>
typealias NewClassName = String

abstract class AbstractCollisionResolver {
    abstract fun resolveDuplicatedNames(existingNode: ClassNode, newNode: ClassNode): ResolvedNodeNames

    abstract fun resolveSquashName(existingNode: ClassNode, newNode: ClassNode): NewClassName
}

class ManualCollisionResolver(
    private val uiDelegate: UIDelegate
): AbstractCollisionResolver() {
    override fun resolveSquashName(existingNode: ClassNode, newNode: ClassNode): NewClassName {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun resolveDuplicatedNames(existingNode: ClassNode, newNode: ClassNode): ResolvedNodeNames {
        TODO()
//        uiDelegate.showCollisionDialog()
    }

    private fun ClassNode.buildCollisionPreview(): String =
        generateSequence(parent as ClassNode) { it.parent as ClassNode }.toList()
            // json preview forehead, json preview tail, last index (for inserting target body)
            .foldRightIndexed(Triple(StringBuilder(), LinkedList<String>(), 0)) { i, parent, collector ->
                val (forehead, tail) = collector
                val gap = "\t\t".repeat(i)
                forehead
                    .append(gap).append(parent.className).append(" : {\n")
                    .append(gap).append("...\n")

                tail.add("$gap...\n$gap}\n")
                collector
            }.let { (builder, tailList, lastIndex) ->
                val gap = "\t\t".repeat(lastIndex + 1)

                childs.forEach {
                    builder.append(gap).append(it.className).append(" : ...\n")
                }

                tailList.foldRight(builder) { tail, acc ->
                    acc.append(tail)
                }

                builder.toString()
            }
            .toString()
}

class AutomaticCollisionResolver: AbstractCollisionResolver() {
    override fun resolveSquashName(existingNode: ClassNode, newNode: ClassNode): NewClassName =
        existingNode.className

    override fun resolveDuplicatedNames(existingNode: ClassNode, newNode: ClassNode): ResolvedNodeNames =
        ResolvedNodeNames(existingNode.collectParentNames(), newNode.collectParentNames())

    private fun ClassNode.collectParentNames(): String {
        val builder = StringBuilder()
        var currentParent = parent
        while (currentParent != null) {
            builder.append(0, currentParent.className)
            currentParent = currentParent.parent
        }
        return builder.toString()
    }
}