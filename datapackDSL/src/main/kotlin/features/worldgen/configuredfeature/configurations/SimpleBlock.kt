package features.worldgen.configuredfeature.configurations

import features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import features.worldgen.configuredfeature.blockstateprovider.simpleStateProvider
import kotlinx.serialization.Serializable

@Serializable
data class SimpleBlock(
	var toPlace: BlockStateProvider = simpleStateProvider(),
) : FeatureConfig()

fun simpleBlock(toPlace: BlockStateProvider = simpleStateProvider()) = SimpleBlock(toPlace)
