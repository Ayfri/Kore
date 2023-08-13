package features.worldgen.placedfeature.modifiers

import features.worldgen.placedfeature.PlacedFeature
import kotlinx.serialization.Serializable

@Serializable
data class RarityFilter(
	var chance: Int = 0,
) : PlacementModifier()

fun PlacedFeature.rarityFilter(chance: Int = 0) {
	placementModifiers += RarityFilter(chance)
}
