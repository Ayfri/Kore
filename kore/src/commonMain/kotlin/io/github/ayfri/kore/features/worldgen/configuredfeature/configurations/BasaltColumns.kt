package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProviderScope
import io.github.ayfri.kore.features.worldgen.intproviders.constant
import io.github.ayfri.kore.generated.arguments.worldgen.types.FeatureArgument
import kotlinx.serialization.Serializable

@Serializable
data class BasaltColumns(
	var reach: IntProvider = ConstantIntProvider(0),
	var height: IntProvider = ConstantIntProvider(0),
) : FeatureConfig(), IntProviderScope

fun ConfiguredFeatures.basaltColumns(
	fileName: String,
	reach: IntProvider = ConstantIntProvider(0),
	height: IntProvider = ConstantIntProvider(0),
): FeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, BasaltColumns(reach, height))
	dp.configuredFeatures += configuredFeature
	return FeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}

fun ConfiguredFeatures.basaltColumns(fileName: String, reach: Int, height: Int): FeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, BasaltColumns(constant(reach), constant(height)))
	dp.configuredFeatures += configuredFeature
	return FeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}
