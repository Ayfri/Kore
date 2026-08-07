package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.simpleStateProvider
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import kotlinx.serialization.Serializable

@Serializable
data class BlockPile(
	var stateProvider: BlockStateProvider = simpleStateProvider(),
) : FeatureConfig()

fun ConfiguredFeatures.blockPile(fileName: String, stateProvider: BlockStateProvider = simpleStateProvider()): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, BlockPile(stateProvider))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}
