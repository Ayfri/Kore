package features.worldgen.configuredfeature.configurations

import features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import features.worldgen.configuredfeature.blockstateprovider.simpleStateProvider
import kotlinx.serialization.Serializable

@Serializable
data class NetherForestVegetation(
	var stateProvider: BlockStateProvider = simpleStateProvider(),
	var spreadWidth: Int = 0,
	var spreadHeight: Int = 0,
) : FeatureConfig()

fun netherForestVegetation(
	stateProvider: BlockStateProvider = simpleStateProvider(),
	spreadWidth: Int = 0,
	spreadHeight: Int = 0,
	block: NetherForestVegetation.() -> Unit = {},
) = NetherForestVegetation(stateProvider, spreadWidth, spreadHeight).apply(block)
