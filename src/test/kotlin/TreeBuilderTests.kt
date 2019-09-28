import com.azoft.json2dart.delegates.generator.tree.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import ideals.TestCollisionResolver
import ideals.Match
import org.junit.Test
import java.io.File

class TreeBuilderTests {

    private val rootNameDefault = "response"

    @Test
    fun testTreeBuilderSimple() {
        val rootNode = extractNodes("$jsonInitialPath/$simpleJson")
        rootNode as ClassNode
        assert(rootNode.containsAllPrimitives())
    }

    @Test
    fun testTreeBuilderInner() {
        val rootNode = extractNodes("$jsonInitialPath/$innerObjectJson")
        rootNode as ClassNode
        assert(rootNode.childs.size == 1)

        rootNode.childs[0].let { innerNode ->

            innerNode as ClassNode
            assert(innerNode.childs.size == 5)
            assert(innerNode.containsAllPrimitives())

            innerNode.childs.find { it is ClassNode }?.let { secondInnerNode ->

                secondInnerNode as ClassNode
                assert(secondInnerNode.childs.size == 4)
                assert(secondInnerNode.containsAllPrimitives())

            } ?: throw Exception("Cannot find second inner class node")
        }
    }

    @Test
    fun testTreeBuilderList() {
        val rootNode = extractNodes("$jsonInitialPath/$listJson")
        rootNode as ClassNode
        assert(rootNode.childs.find { it !is ListNode } == null)
        rootNode.childs.map { it as ListNode }.apply {
            find { it.value is IntNode } ?: Exception("Cannot find int list")
            find { it.value is StringNode } ?: Exception("Cannot find strings list")
            find { it.value is DoubleNode } ?: Exception("Cannot find double list")
            find { it.value is NullNode } ?: Exception("Cannot find empty list")
            find { it.value is ClassNode }?.value?.let { classNode ->
                classNode as ClassNode
                assert(classNode.containsAllPrimitives())
            } ?: Exception("Cannot find class list")
        }
    }

    @Test
    fun testCollisionCase() {
        val firstInnerName = "FirstInner"
        val secondInnerName = "SecondInner"
        val rootNode = extractNodes(
            jsonPath = "$jsonInitialPath/$collisionJson",
            collisionResolver = TestCollisionResolver(
                listOf(
                    Match("inner", "inner", firstInnerName, secondInnerName)
                )
            )
        )
        rootNode as ClassNode
        assert(rootNode.containsAllPrimitives())
        val secondInnerNode = rootNode.childs.find { it.name == secondInnerName }
            ?: throw Exception("Cannot find second inner class node")
        secondInnerNode as ClassNode

        val firstInnerNode = secondInnerNode.childs.find { it.name == firstInnerName }
            ?: throw Exception("Cannot find first inner class node")

        firstInnerNode as ClassNode
        firstInnerNode.containsAllPrimitives()
    }

    private fun extractNodes(
        jsonPath: String,
        rootName: String = rootNameDefault,
        collisionResolver: AbstractCollisionResolver = TestCollisionResolver(listOf())
    ): Node =
        JsonNodeConverter(
            collisionResolver
        ).extractNodes(
            rootName,
            jacksonObjectMapper().readTree(File(jsonPath).readText())
        )

    private fun ClassNode.containsAllPrimitives() =
        childs.find { it is NullNode } != null
            && childs.find { it is DoubleNode } != null
            && childs.find { it is StringNode } != null
            && childs.find { it is IntNode } != null
}