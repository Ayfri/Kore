package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import kotlinx.serialization.Serializable

@Serializable
data class Bamboo(
	var probability: Double = 0.0,
) : FeatureConfig()

fun ConfiguredFeatures.bamboo(fileName: String, probability: Double): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, Bamboo(probability))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}
