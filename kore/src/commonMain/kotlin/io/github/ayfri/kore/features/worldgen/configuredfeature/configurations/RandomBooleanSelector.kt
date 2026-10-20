package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
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
) = dp.configuredFeature(fileName, RandomBooleanSelector(featureFalse, featureTrue))
