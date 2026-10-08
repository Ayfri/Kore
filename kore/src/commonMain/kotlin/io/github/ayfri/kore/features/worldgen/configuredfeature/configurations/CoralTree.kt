package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.generated.arguments.worldgen.types.FeatureArgument
import kotlinx.serialization.Serializable

@Serializable
data object CoralTree : FeatureConfig()

fun ConfiguredFeatures.coralTree(fileName: String): FeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, CoralTree)
	dp.configuredFeatures += configuredFeature
	return FeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}
