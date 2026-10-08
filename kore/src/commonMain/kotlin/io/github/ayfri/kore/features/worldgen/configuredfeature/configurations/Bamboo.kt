package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.generated.arguments.worldgen.types.FeatureArgument
import kotlinx.serialization.Serializable

@Serializable
data class Bamboo(
	var probability: Double = 0.0,
) : FeatureConfig()

fun ConfiguredFeatures.bamboo(fileName: String, probability: Double): FeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, Bamboo(probability))
	dp.configuredFeatures += configuredFeature
	return FeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}
