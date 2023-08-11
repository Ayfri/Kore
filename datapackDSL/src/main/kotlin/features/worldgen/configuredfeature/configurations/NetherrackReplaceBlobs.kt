package features.worldgen.configuredfeature.configurations

import data.block.BlockState
import data.block.blockStateStone
import features.worldgen.intproviders.IntProvider
import features.worldgen.intproviders.constant
import kotlinx.serialization.Serializable

@Serializable
data class NetherrackReplaceBlobs(
	var state: BlockState = blockStateStone(),
	var target: BlockState = blockStateStone(),
	var radius: IntProvider = constant(0),
) : FeatureConfig()

fun netherrackReplaceBlobs(
	state: BlockState = blockStateStone(),
	target: BlockState = blockStateStone(),
	radius: IntProvider = constant(0),
	block: NetherrackReplaceBlobs.() -> Unit = {},
) = NetherrackReplaceBlobs(state, target, radius).apply(block)
