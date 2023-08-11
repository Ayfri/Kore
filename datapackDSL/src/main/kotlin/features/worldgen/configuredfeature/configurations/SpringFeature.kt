package features.worldgen.configuredfeature.configurations

import arguments.types.BlockOrTagArgument
import data.block.BlockState
import data.block.blockStateStone
import serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class SpringFeature(
	var state: BlockState = blockStateStone(),
	var rockCount: Int = 0,
	var holeCount: Int = 0,
	var requiresBlockBelow: Boolean = false,
	var validBlocks: InlinableList<BlockOrTagArgument> = emptyList(),
) : FeatureConfig()

fun springFeature(block: SpringFeature.() -> Unit = {}) = SpringFeature().apply(block)
