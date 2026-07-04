package io.github.ayfri.kore.features.worldgen.placedfeature.modifiers

import io.github.ayfri.kore.features.worldgen.HeightMap
import io.github.ayfri.kore.features.worldgen.placedfeature.PlacedFeature
import kotlinx.serialization.Serializable

@Serializable
data class HeightMap(
	var heightmap: HeightMap,
) : PlacementModifier()

fun PlacedFeature.heightMap(height: HeightMap) {
	placementModifiers += HeightMap(height)
}
