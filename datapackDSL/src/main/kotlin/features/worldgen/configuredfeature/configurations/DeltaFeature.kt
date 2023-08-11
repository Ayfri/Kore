package features.worldgen.configuredfeature.configurations

import data.block.BlockState
import data.block.blockStateStone
import features.worldgen.intproviders.IntProvider
import features.worldgen.intproviders.constant
import kotlinx.serialization.Serializable

@Serializable
data class DeltaFeature(
	var content: BlockState = blockStateStone(),
	var rim: BlockState = blockStateStone(),
	var size: IntProvider = constant(0),
	var rimSize: IntProvider = constant(0),
) : FeatureConfig()

fun deltaFeature(
	content: BlockState = blockStateStone(),
	rim: BlockState = blockStateStone(),
	size: IntProvider = constant(0),
	rimSize: IntProvider = constant(0),
	block: DeltaFeature.() -> Unit = {},
) = DeltaFeature(content, rim, size, rimSize).apply(block)

fun deltaFeature(
	content: BlockState = blockStateStone(),
	rim: BlockState = blockStateStone(),
	size: Int,
	rimSize: Int,
) = DeltaFeature(content, rim, constant(size), constant(rimSize))
