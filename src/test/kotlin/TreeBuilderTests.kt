import com.azoft.json2dart.delegates.generator.tree.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import ideals.TestCollisionResolver
import ideals.Match
import org.junit.Test
import java.io.File

class TreeBuilderTests {

    private val rootNameDefault = "response"

//    @Test
//    fun testTreeBuilderSimple() {
//        extractNodes("$jsonInitialPath/$simpleJson").apply {
//            assert(size == 1)
//            get(0).let { node ->
//                node as ClassNode
//                assert(node.childs.size == 4)
//                assert(node.containsAllPrimitives())
//            }
//        }
//    }
//
//    @Test
//    fun testTreeBuilderInner() {
//        extractNodes("$jsonInitialPath/$innerObjectJson").apply {
//            assert(size == 3)
//
//            find { it.name == rootNameDefault }.let { rootNode ->
//                rootNode as ClassNode
//                assert(rootNode.childs.size == 1)
//
//                rootNode.childs[0].let { innerNode ->
//
//                    innerNode as ClassNode
//                    assert(innerNode.childs.size == 5)
//                    assert(innerNode.containsAllPrimitives())
//
//                    innerNode.childs.find { it is ClassNode }?.let { secondInnerNode ->
//
//                        secondInnerNode as ClassNode
//                        assert(secondInnerNode.childs.size == 4)
//                        assert(secondInnerNode.containsAllPrimitives())
//
//                    } ?: throw Exception("Cannot find second inner class node")
//                }
//            }
//        }
//    }
//
//    @Test
//    fun testTreeBuilderList() {
//        extractNodes("$jsonInitialPath/$listJson").let { extractedNodes ->
//            assert(extractedNodes.size == 2)
//            extractedNodes.map { it as ClassNode }.apply {
//
//                find { it.name == rootNameDefault }?.childs?.map { it as ListNode }?.apply {
//
//                    find { it.value is IntNode } ?: Exception("Cannot find int list")
//                    find { it.value is StringNode } ?: Exception("Cannot find strings list")
//                    find { it.value is DoubleNode } ?: Exception("Cannot find double list")
//                    find { it.value is NullNode } ?: Exception("Cannot find empty list")
//                    find { it.value is ClassNode }?.value?.let { classNode ->
//                        classNode as ClassNode
//                        assert(classNode.containsAllPrimitives())
//                    } ?: Exception("Cannot find class list")
//                }
//            }
//        }
//    }

    @Test
    fun testCollisionCase() {
        val firstInnerName = "FirstInner"
        val secondInnerName = "SecondInner"
        extractNodes(
            jsonPath = "$jsonInitialPath/$collisionJson",
            collisionResolver = TestCollisionResolver(
                listOf(
                    Match("inner", "inner", firstInnerName, secondInnerName)
                )
            )
        ).also { (rootAddress, memory) ->

            val rootNode = memory[rootAddress]
                ?: throw Exception("Cannot find root class node")

            rootNode as ClassNode
            rootNode.containsAllPrimitives(memory)

            val secondInnerNode =
                memory[
                    rootNode.childs.find { memory[it]?.name == secondInnerName }
                        ?: throw Exception("Cannot find second inner class node")
                ]
            secondInnerNode as ClassNode

            println()
            val firstInnerNode =
                memory[
                    secondInnerNode.childs.find { memory[it]?.name == firstInnerName }
                        ?: throw Exception("Cannot find first inner class node")
                ]

            firstInnerNode as ClassNode
            firstInnerNode.containsAllPrimitives(memory)
        }
    }

    private fun extractNodes(
        jsonPath: String,
        rootName: String = rootNameDefault,
        collisionResolver: AbstractCollisionResolver = TestCollisionResolver(listOf())
    ): Pair<VirtualAddress, VirtualMemory> =
        JsonNodeConverter(
            collisionResolver
        ).extractNodes(
            rootName,
            jacksonObjectMapper().readTree(File(jsonPath).readText())
        )

    private fun ClassNode.containsAllPrimitives(memory: VirtualMemory) =
        childs.find { memory[it] is NullNode } != null
            && childs.find { memory[it] is DoubleNode } != null
            && childs.find { memory[it] is StringNode } != null
            && childs.find { memory[it] is IntNode } != null

    private fun VirtualMemory.extractChilds(node: ClassNode) =
        node.childs.map { this[it] }
}