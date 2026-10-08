package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.generated.arguments.worldgen.types.FeatureArgument
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
): FeatureArgument {
	val configuredFeature = ConfiguredFeature(
		fileName,
		UnderwaterMagma(floorSearchRange, placementRadiusAroundFloor, placementProbabilityPerValidPosition).apply(block),
	)
	dp.configuredFeatures += configuredFeature
	return FeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}
