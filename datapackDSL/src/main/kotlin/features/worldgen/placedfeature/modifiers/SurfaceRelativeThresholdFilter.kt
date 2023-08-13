package features.worldgen.placedfeature.modifiers

import features.worldgen.HeightMap
import features.worldgen.placedfeature.PlacedFeature
import kotlinx.serialization.Serializable

@Serializable
data class SurfaceRelativeThresholdFilter(
	var heightMap: HeightMap,
	var minInclusive: Int? = null,
	var maxInclusive: Int? = null,
) : PlacementModifier()

fun PlacedFeature.surfaceRelativeThresholdFilter(
	heightMap: HeightMap,
	minInclusive: Int? = null,
	maxInclusive: Int? = null,
	block: SurfaceRelativeThresholdFilter.() -> Unit = {},
) {
	placementModifiers += SurfaceRelativeThresholdFilter(heightMap, minInclusive, maxInclusive).apply(block)
}
