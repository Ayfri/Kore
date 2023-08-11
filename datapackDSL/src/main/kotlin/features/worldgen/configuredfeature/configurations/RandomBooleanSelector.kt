package features.worldgen.configuredfeature.configurations

import arguments.types.resources.worldgen.PlacedFeatureArgument
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
