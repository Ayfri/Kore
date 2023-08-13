package features.worldgen.placedfeature.modifiers

import features.worldgen.placedfeature.PlacedFeature
import kotlinx.serialization.Serializable

@Serializable
data class SurfaceWaterDepthFilter(
	var maxWaterDepth: Int = 0,
) : PlacementModifier()

fun PlacedFeature.surfaceWaterDepthFilter(maxWaterDepth: Int = 0) {
	placementModifiers += SurfaceWaterDepthFilter(maxWaterDepth)
}
