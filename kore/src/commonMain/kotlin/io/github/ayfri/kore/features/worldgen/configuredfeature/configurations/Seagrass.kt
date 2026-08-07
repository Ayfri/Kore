package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import kotlinx.serialization.Serializable

@Serializable
data class Seagrass(
	var probability: Double = 0.0,
) : FeatureConfig()

fun ConfiguredFeatures.seagrass(fileName: String, probability: Double = 0.0): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, Seagrass(probability))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}
