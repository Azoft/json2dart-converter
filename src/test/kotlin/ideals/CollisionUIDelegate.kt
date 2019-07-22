package ideals

import com.azoft.json2dart.delegates.ui.UIDelegate

class CollisionUIDelegate(
    private val matches: List<Match>
) : UIDelegate {
    override fun showDialogSquash(
        firstSample: String,
        firstName: String,
        secondSample: String,
        secondName: String,
        onEnterName: (String, String) -> Unit,
        onNotSquash: () -> Unit
    ) {
        matches
            .find { (leftName, rightName, _, _) ->
                firstName == leftName && secondName == rightName
            }?.let { (_, _, leftResolvedName, rightResolvedName) ->
                onEnterName(leftResolvedName, rightResolvedName)
            } ?: throw NotImplementedError("Cannot find match for $firstName, $secondName")
    }

}

data class Match(
    val leftName: String,
    val rightName: String,
    val leftResolvedName: String,
    val rightResolvedName: String
)