package io.github.ayfri.kore.features.worldgen.blockpredicate

import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.block.blockStateStone
import io.github.ayfri.kore.serializers.TripleAsArray
import kotlinx.serialization.Serializable

@Serializable
data class WouldSurvive(
	var offset: TripleAsArray<Int, Int, Int>? = null,
	var state: BlockState = blockStateStone(),
) : BlockPredicate()

fun wouldSurvive(offset: TripleAsArray<Int, Int, Int>? = null, state: BlockState = blockStateStone()) =
	WouldSurvive(offset, state)

fun MutableList<BlockPredicate>.wouldSurvive(
	offset: TripleAsArray<Int, Int, Int>? = null,
	state: BlockState = blockStateStone(),
) {
	this += WouldSurvive(offset, state)
}
