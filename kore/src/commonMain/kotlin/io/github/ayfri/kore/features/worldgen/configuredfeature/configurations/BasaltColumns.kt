package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.constant
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import kotlinx.serialization.Serializable

@Serializable
data class BasaltColumns(
	var reach: IntProvider = constant(0),
	var height: IntProvider = constant(0),
) : FeatureConfig()

fun ConfiguredFeatures.basaltColumns(
	fileName: String,
	reach: IntProvider = constant(0),
	height: IntProvider = constant(0),
): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, BasaltColumns(reach, height))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}

fun ConfiguredFeatures.basaltColumns(fileName: String, reach: Int, height: Int): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, BasaltColumns(constant(reach), constant(height)))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}
