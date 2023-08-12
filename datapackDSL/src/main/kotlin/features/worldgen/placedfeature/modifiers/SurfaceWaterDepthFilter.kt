package features.worldgen.placedfeature.modifiers

import kotlinx.serialization.Serializable

@Serializable
data class SurfaceWaterDepthFilter(
	var maxWaterDepth: Int = 0,
) : PlacementModifier()

fun surfaceWaterDepthFilter(maxWaterDepth: Int = 0) = SurfaceWaterDepthFilter(maxWaterDepth)

fun MutableList<PlacementModifier>.surfaceWaterDepthFilter(maxWaterDepth: Int = 0) {
	this += SurfaceWaterDepthFilter(maxWaterDepth)
}
