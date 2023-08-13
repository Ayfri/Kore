package features.worldgen.placedfeature.modifiers

import features.worldgen.HeightMap
import features.worldgen.placedfeature.PlacedFeature
import kotlinx.serialization.Serializable

@Serializable
data class HeightMap(
	var heightmap: HeightMap,
) : PlacementModifier()

fun PlacedFeature.heightMap(height: HeightMap) {
	placementModifiers += HeightMap(height)
}
