package features.worldgen.placedfeature.modifiers

import kotlinx.serialization.Serializable

@Serializable
data class RarityFilter(
	var chance: Int = 0,
) : PlacementModifier()

fun rarityFilter(chance: Int = 0) = RarityFilter(chance)

fun MutableList<PlacementModifier>.rarityFilter(chance: Int = 0) {
	this += RarityFilter(chance)
}
