package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import kotlinx.serialization.Serializable

@Serializable
data object VoidStartPlatform : FeatureConfig()

fun ConfiguredFeatures.voidStartPlatform(fileName: String): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, VoidStartPlatform)
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}
