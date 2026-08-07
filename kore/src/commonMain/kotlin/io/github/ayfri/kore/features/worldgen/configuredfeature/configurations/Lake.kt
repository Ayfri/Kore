package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.block.blockStateStone
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicate
import io.github.ayfri.kore.features.worldgen.blockpredicate.True
import kotlinx.serialization.Serializable

@Serializable
data class Lake(
	var fluid: BlockState = blockStateStone(),
	var barrier: BlockState = blockStateStone(),
	var canPlaceFeature: BlockPredicate = True,
	var canReplaceWithAirOrFluid: BlockPredicate = True,
	var canReplaceWithBarrier: BlockPredicate = True,
) : FeatureConfig()

fun lake(
	fluid: BlockState = blockStateStone(),
	barrier: BlockState = blockStateStone(),
	canPlaceFeature: BlockPredicate = True,
	canReplaceWithAirOrFluid: BlockPredicate = True,
	canReplaceWithBarrier: BlockPredicate = True,
	block: Lake.() -> Unit = {},
) = Lake(fluid, barrier, canPlaceFeature, canReplaceWithAirOrFluid, canReplaceWithBarrier).apply(block)
