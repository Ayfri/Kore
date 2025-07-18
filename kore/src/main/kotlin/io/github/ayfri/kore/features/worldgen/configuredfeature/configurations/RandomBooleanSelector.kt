package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.generated.arguments.worldgen.types.PlacedFeatureArgument
import kotlinx.serialization.Serializable

@Serializable
data class RandomBooleanSelector(
	var featureFalse: PlacedFeatureArgument,
	var featureTrue: PlacedFeatureArgument,
) : FeatureConfig()

fun randomBooleanSelector(
	featureFalse: PlacedFeatureArgument,
	featureTrue: PlacedFeatureArgument,
) = RandomBooleanSelector(featureFalse, featureTrue)
