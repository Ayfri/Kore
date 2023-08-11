package features.worldgen.configuredfeature.configurations

import kotlinx.serialization.Serializable

@Serializable
data class UnderwaterMagma(
	var floorSearchRange: Int = 0,
	var placementRadiusAroundFloor: Int = 0,
	var placementProbabilityPerValidPosition: Double = 0.0,
) : FeatureConfig()

fun underwaterMagma(
	floorSearchRange: Int = 0,
	placementRadiusAroundFloor: Int = 0,
	placementProbabilityPerValidPosition: Double = 0.0,
	block: UnderwaterMagma.() -> Unit = {},
) = UnderwaterMagma(floorSearchRange, placementRadiusAroundFloor, placementProbabilityPerValidPosition).apply(block)
