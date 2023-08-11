package features.worldgen.configuredfeature.configurations

import arguments.types.resources.worldgen.PlacedFeatureArgument
import kotlinx.serialization.Serializable

@Serializable
data class RandomSelector(
	var features: List<RandomSelectorFeature> = emptyList(),
	var default: PlacedFeatureArgument? = null,
) : FeatureConfig()

@Serializable
data class RandomSelectorFeature(
	var chance: Float? = null,
	var feature: PlacedFeatureArgument,
)
