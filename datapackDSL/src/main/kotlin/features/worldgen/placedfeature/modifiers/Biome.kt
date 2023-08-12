package features.worldgen.placedfeature.modifiers

import kotlinx.serialization.Serializable

@Serializable
data object Biome : PlacementModifier()

fun MutableList<PlacementModifier>.biome() {
	this += Biome
}
