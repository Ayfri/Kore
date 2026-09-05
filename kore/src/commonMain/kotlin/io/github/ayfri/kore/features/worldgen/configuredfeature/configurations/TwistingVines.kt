package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import kotlinx.serialization.Serializable

@Serializable
data class TwistingVines(
	var spreadWidth: Int = 0,
	var spreadHeight: Int = 0,
	var maxHeight: Int = 0,
) : FeatureConfig()

fun ConfiguredFeatures.twistingVines(
	fileName: String,
	spreadWidth: Int = 0,
	spreadHeight: Int = 0,
	maxHeight: Int = 0,
	block: TwistingVines.() -> Unit = {},
): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, TwistingVines(spreadWidth, spreadHeight, maxHeight).apply(block))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}
