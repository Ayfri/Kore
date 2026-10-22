package io.github.ayfri.kore.features.worldgen.placedfeature.modifiers

import kotlinx.serialization.Serializable

@Serializable
data class SurfaceWaterDepthFilter(
	var maxWaterDepth: Int = 0,
) : PlacementModifier()

fun PlacementModifiersScope.surfaceWaterDepthFilter(maxWaterDepth: Int = 0) {
	placementModifiers += SurfaceWaterDepthFilter(maxWaterDepth)
}
