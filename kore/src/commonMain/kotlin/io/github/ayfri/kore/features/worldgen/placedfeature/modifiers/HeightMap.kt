package io.github.ayfri.kore.features.worldgen.placedfeature.modifiers

import io.github.ayfri.kore.features.worldgen.HeightMap
import kotlinx.serialization.Serializable

@Serializable
data class HeightMap(
	var heightmap: HeightMap,
) : PlacementModifier()

fun PlacementModifiersScope.heightMap(height: HeightMap) {
	placementModifiers += HeightMap(height)
}
