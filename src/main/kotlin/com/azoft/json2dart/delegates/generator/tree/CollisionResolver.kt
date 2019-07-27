package com.azoft.json2dart.delegates.generator.tree

import com.azoft.json2dart.delegates.ui.UIDelegate

typealias ResolvedNodes = Pair<ClassNode, ClassNode>

abstract class AbstractCollisionResolver {
    abstract fun resolve(existingNode: ClassNode, newNode: ClassNode): ResolvedNodes
}

class ManualCollisionResolver(
    private val uiDelegate: UIDelegate
): AbstractCollisionResolver() {
    override fun resolve(existingNode: ClassNode, newNode: ClassNode): ResolvedNodes {
        TODO()
    }
}

class AutomaticCollisionResolver: AbstractCollisionResolver() {
    override fun resolve(existingNode: ClassNode, newNode: ClassNode): ResolvedNodes {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}