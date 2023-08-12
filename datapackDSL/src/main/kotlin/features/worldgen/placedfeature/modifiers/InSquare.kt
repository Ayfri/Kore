package features.worldgen.placedfeature.modifiers

import kotlinx.serialization.Serializable

@Serializable
data object InSquare : PlacementModifier()

fun MutableList<PlacementModifier>.inSquare() {
	this += InSquare
}
