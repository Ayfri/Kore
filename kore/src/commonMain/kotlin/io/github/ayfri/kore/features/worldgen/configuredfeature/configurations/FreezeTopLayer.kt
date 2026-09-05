package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import kotlinx.serialization.Serializable

@Serializable
data object FreezeTopLayer : FeatureConfig()

fun ConfiguredFeatures.freezeTopLayer(fileName: String): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, FreezeTopLayer)
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}
