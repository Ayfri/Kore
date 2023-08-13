package features.worldgen.placedfeature.modifiers

import features.worldgen.placedfeature.PlacedFeature
import kotlinx.serialization.Serializable

@Serializable
data object Biome : PlacementModifier()

fun PlacedFeature.biome() {
	placementModifiers += Biome
}
