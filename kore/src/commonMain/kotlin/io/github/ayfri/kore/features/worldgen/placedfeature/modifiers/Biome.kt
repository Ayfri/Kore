package io.github.ayfri.kore.features.worldgen.placedfeature.modifiers

import kotlinx.serialization.Serializable

@Serializable
data object Biome : PlacementModifier()

fun PlacementModifiersScope.biome() {
	placementModifiers += Biome
}
