package features.worldgen.placedfeature.modifiers

import features.worldgen.HeightMap
import kotlinx.serialization.Serializable

@Serializable
data class HeightMap(
	var heightmap: HeightMap,
) : PlacementModifier()

fun heightMap(height: HeightMap) = HeightMap(height)

fun MutableList<PlacementModifier>.heightMap(height: HeightMap) {
	this += HeightMap(height)
}
