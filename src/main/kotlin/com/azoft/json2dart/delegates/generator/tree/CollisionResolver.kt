package com.azoft.json2dart.delegates.generator.tree

import com.azoft.json2dart.delegates.ui.UIDelegate
import java.lang.StringBuilder

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
    }
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
            builder.append(currentParent.className)
            currentParent = currentParent.parent
        }
        return builder.toString()
    }
}