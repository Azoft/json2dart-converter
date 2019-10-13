package ideals

import com.azoft.json2dart.delegates.generator.tree.AbstractCollisionResolver
import com.azoft.json2dart.delegates.generator.tree.ClassNode
import com.azoft.json2dart.delegates.generator.tree.NewClassName
import com.azoft.json2dart.delegates.generator.tree.ResolvedNodeNames

class TestCollisionResolver(
    private val resolveMatches: List<CollisionMatch> = listOf(),
    private val squashMatches: List<SquashMatch> = listOf()
) : AbstractCollisionResolver() {
    override fun resolveDuplicatedNames(existingNode: ClassNode, newNode: ClassNode): ResolvedNodeNames {
        val firstName = existingNode.className
        val secondName = newNode.className
        return resolveMatches
            .find { (leftName, rightName, _, _) ->
                firstName == leftName && secondName == rightName
            }?.let { (_, _, leftResolvedName, rightResolvedName) ->
                leftResolvedName to rightResolvedName
            } ?: throw NotImplementedError("Cannot find match for $firstName, $secondName")
    }

    override fun resolveSquashName(existingNode: ClassNode, newNode: ClassNode): NewClassName {
        val existingName = existingNode.className
        val newName = newNode.className
        return squashMatches
            .find { (leftName, rightName, _ ) ->
                leftName == existingName && rightName == newName
            }?.let { (_, _, newName) -> newName }
            ?: throw NotImplementedError("Cannot find match for $existingName, $newName")
    }
}

data class CollisionMatch(
    val leftName: String,
    val rightName: String,
    val leftResolvedName: String,
    val rightResolvedName: String
)

data class SquashMatch(
    val leftName: String,
    val rightName: String,
    val newClassName: NewClassName
)