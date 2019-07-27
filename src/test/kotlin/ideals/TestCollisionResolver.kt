package ideals

import com.azoft.json2dart.delegates.generator.tree.AbstractCollisionResolver
import com.azoft.json2dart.delegates.generator.tree.ClassNode
import com.azoft.json2dart.delegates.generator.tree.ResolvedNodes
import com.azoft.json2dart.delegates.generator.tree.copy

class TestCollisionResolver(
    private val matches: List<Match>
) : AbstractCollisionResolver() {
    override fun resolve(existingNode: ClassNode, newNode: ClassNode): ResolvedNodes {
        val firstName = existingNode.name
        val secondName = newNode.name
        return matches
            .find { (leftName, rightName, _, _) ->
                firstName == leftName && secondName == rightName
            }?.let { (_, _, leftResolvedName, rightResolvedName) ->
                existingNode.copy(name = leftResolvedName) to newNode.copy(name = rightResolvedName)
            } ?: throw NotImplementedError("Cannot find match for $firstName, $secondName")
    }

}

data class Match(
    val leftName: String,
    val rightName: String,
    val leftResolvedName: String,
    val rightResolvedName: String
)