package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.Target
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import kotlinx.serialization.Serializable

@Serializable
data class ReplaceSingleBlock(
	var targets: List<Target> = emptyList(),
) : FeatureConfig()

fun ConfiguredFeatures.replaceSingleBlock(fileName: String, targets: List<Target> = emptyList()): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, ReplaceSingleBlock(targets))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}

fun ConfiguredFeatures.replaceSingleBlock(fileName: String, vararg targets: Target): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, ReplaceSingleBlock(targets.toList()))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}
