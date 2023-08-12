package features.worldgen.configuredfeature.configurations

import features.worldgen.blockpredicate.BlockPredicate
import features.worldgen.blockpredicate.True
import features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import features.worldgen.configuredfeature.blockstateprovider.simpleStateProvider
import kotlinx.serialization.Serializable

@Serializable
data class HugeFungus(
	var hatState: BlockStateProvider = simpleStateProvider(),
	var decorState: BlockStateProvider = simpleStateProvider(),
	var stemState: BlockStateProvider = simpleStateProvider(),
	var validBaseBlock: BlockStateProvider = simpleStateProvider(),
	var replaceableBlocks: BlockPredicate = True,
	var planted: Boolean? = null,
) : FeatureConfig()

fun hugeFungus(block: HugeFungus.() -> Unit = {}) = HugeFungus().apply(block)
