package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.generated.arguments.worldgen.types.PlacedFeatureArgument
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
