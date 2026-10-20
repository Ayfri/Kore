package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import kotlinx.serialization.Serializable

@Serializable
data class UnderwaterMagma(
	var floorSearchRange: Int = 0,
	var placementRadiusAroundFloor: Int = 0,
	var placementProbabilityPerValidPosition: Double = 0.0,
) : FeatureConfig()

fun ConfiguredFeatures.underwaterMagma(
	fileName: String,
	floorSearchRange: Int = 0,
	placementRadiusAroundFloor: Int = 0,
	placementProbabilityPerValidPosition: Double = 0.0,
	block: UnderwaterMagma.() -> Unit = {},
) = dp.configuredFeature(
	fileName,
	UnderwaterMagma(floorSearchRange, placementRadiusAroundFloor, placementProbabilityPerValidPosition).apply(block),
)
