package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.PlacedFeatureArgument
import kotlinx.serialization.Serializable

@Serializable
data class RandomBooleanSelector(
	var featureFalse: PlacedFeatureArgument,
	var featureTrue: PlacedFeatureArgument,
) : FeatureConfig()

fun ConfiguredFeatures.randomBooleanSelector(
	fileName: String,
	featureFalse: PlacedFeatureArgument,
	featureTrue: PlacedFeatureArgument,
): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, RandomBooleanSelector(featureFalse, featureTrue))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}
