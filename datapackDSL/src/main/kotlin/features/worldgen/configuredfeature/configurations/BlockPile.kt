package features.worldgen.configuredfeature.configurations

import features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import features.worldgen.configuredfeature.blockstateprovider.simpleStateProvider
import kotlinx.serialization.Serializable

@Serializable
data class BlockPile(
	var stateProvider: BlockStateProvider = simpleStateProvider(),
) : FeatureConfig()

fun blockPile(stateProvider: BlockStateProvider = simpleStateProvider()) = BlockPile(stateProvider)
