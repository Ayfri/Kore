package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
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

fun RandomSelector.feature(feature: PlacedFeatureArgument, chance: Float? = null) {
	features += RandomSelectorFeature(chance, feature)
}

fun ConfiguredFeatures.randomSelector(
	fileName: String,
	default: PlacedFeatureArgument? = null,
	block: RandomSelector.() -> Unit = {},
): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, RandomSelector(default = default).apply(block))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}
