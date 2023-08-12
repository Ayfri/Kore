package features.worldgen.placedfeature.modifiers

import features.worldgen.HeightMap
import kotlinx.serialization.Serializable

@Serializable
data class SurfaceRelativeThresholdFilter(
	var heightMap: HeightMap,
	var minInclusive: Int? = null,
	var maxInclusive: Int? = null,
) : PlacementModifier()

fun surfaceRelativeThresholdFilter(
	heightMap: HeightMap,
	minInclusive: Int? = null,
	maxInclusive: Int? = null,
	block: SurfaceRelativeThresholdFilter.() -> Unit = {},
) = SurfaceRelativeThresholdFilter(heightMap, minInclusive, maxInclusive).apply(block)

fun MutableList<PlacementModifier>.surfaceRelativeThresholdFilter(
	heightMap: HeightMap,
	minInclusive: Int? = null,
	maxInclusive: Int? = null,
	block: SurfaceRelativeThresholdFilter.() -> Unit = {},
) {
	this += SurfaceRelativeThresholdFilter(heightMap, minInclusive, maxInclusive).apply(block)
}
