package com.azoft.json2dart.delegates.generator.tree

import com.azoft.json2dart.delegates.ui.UIDelegate

typealias ResolvedNodeNames = Pair<String, String>
typealias NewClassName = String

abstract class AbstractCollisionResolver {
    abstract fun resolve(existingNode: ClassNode, newNode: ClassNode): ResolvedNodeNames

    abstract fun resolveSquashName(existingNode: ClassNode, newNode: ClassNode): NewClassName
}

class ManualCollisionResolver(
    private val uiDelegate: UIDelegate
): AbstractCollisionResolver() {
    override fun resolveSquashName(existingNode: ClassNode, newNode: ClassNode): NewClassName {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun resolve(existingNode: ClassNode, newNode: ClassNode): ResolvedNodeNames {
        TODO()
    }
}

class AutomaticCollisionResolver: AbstractCollisionResolver() {
    override fun resolveSquashName(existingNode: ClassNode, newNode: ClassNode): NewClassName {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun resolve(existingNode: ClassNode, newNode: ClassNode): ResolvedNodeNames {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}