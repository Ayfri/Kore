package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.block.blockStateStone
import io.github.ayfri.kore.serializers.InlinableList
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
