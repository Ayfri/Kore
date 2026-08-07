package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.simpleStateProvider
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import kotlinx.serialization.Serializable

@Serializable
data class NetherForestVegetation(
	var stateProvider: BlockStateProvider = simpleStateProvider(),
	var spreadWidth: Int = 0,
	var spreadHeight: Int = 0,
) : FeatureConfig()

fun ConfiguredFeatures.netherForestVegetation(
	fileName: String,
	stateProvider: BlockStateProvider = simpleStateProvider(),
	spreadWidth: Int = 0,
	spreadHeight: Int = 0,
	block: NetherForestVegetation.() -> Unit = {},
): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, NetherForestVegetation(stateProvider, spreadWidth, spreadHeight).apply(block))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}
