package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.simpleStateProvider
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import kotlinx.serialization.Serializable

@Serializable
data class SimpleBlock(
	var toPlace: BlockStateProvider = simpleStateProvider(),
	var scheduleTick: Boolean = false,
) : FeatureConfig()

fun ConfiguredFeatures.simpleBlock(
	fileName: String,
	toPlace: BlockStateProvider = simpleStateProvider(),
	scheduleTick: Boolean = false,
): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, SimpleBlock(toPlace, scheduleTick))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}
