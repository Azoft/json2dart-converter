package com.azoft.json2dart.delegates.generator.tree

class ClassNodeCorrector(
    private val collisionResolver: AbstractCollisionResolver = AutomaticCollisionResolver(),
    private val squash: Boolean = true
) {
    private val nameMap = mutableMapOf<String, ClassNode>()
    private val registeredClasses = RegisteredClassesContainer()

    fun correct(newNode: ClassNode): ClassNode {
        if (squash) {
            processSquash(newNode)
        }
        processNameCollisions(newNode)
        return newNode
    }

    private fun processSquash(newNode: ClassNode) {
        val existedNodes = registeredClasses.getExistingNodes(newNode)

        if (existedNodes.isNullOrEmpty()) {
            registeredClasses.register(newNode)
            return
        }

        if (existedNodes.size == 1) {
            val existedNode = existedNodes[0]

            if (existedNode.parent !== newNode.parent) return

            val className = collisionResolver.resolveSquashName(existedNode, newNode)
            existedNode.fieldName = existedNode.className
            newNode.fieldName = newNode.className
            newNode.className = className
            nameMap.remove(existedNode.className)
            existedNode.className = className
            registeredClasses.register(newNode)
            return
        }

        newNode.fieldName = newNode.className
        newNode.className = existedNodes[0].className
        registeredClasses.register(newNode)
    }

    private fun processNameCollisions(newNode: ClassNode) {
        if (!nameMap.contains(newNode.className)) {
            nameMap[newNode.className] = newNode
            return
        }

        val oldNode = nameMap[newNode.className]!!

        if (oldNode == newNode) {
            return
        }

        nameMap.remove(oldNode.className)
        val (oldResolvedName, newResolvedName) = collisionResolver.resolve(oldNode, newNode)

        registeredClasses.updateClassName(oldNode, oldResolvedName)
        registeredClasses.updateClassName(newNode, newResolvedName)
        nameMap[oldResolvedName] = oldNode
        nameMap[newResolvedName] = newNode
    }

    class RegisteredClassesContainer {

        private val registered = mutableMapOf<ClassNode, MutableList<ClassNode>>()

        fun getExistingNodes(sample: ClassNode): List<ClassNode>? = registered[sample]

        fun register(node: ClassNode) {
            val existing = registered[node]
            if (existing.isNullOrEmpty()) {
                registered[node] = mutableListOf(node)
                return
            }
            existing.add(node)
        }

        fun updateClassName(node: ClassNode, newClassName: String) {
            node.className = newClassName
        }
    }
}