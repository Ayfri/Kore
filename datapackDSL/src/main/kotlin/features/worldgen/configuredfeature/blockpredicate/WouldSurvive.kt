package features.worldgen.configuredfeature.blockpredicate

import data.block.BlockState
import data.block.blockStateStone
import serializers.TripleAsArray
import kotlinx.serialization.Serializable

@Serializable
data class WouldSurvive(
	var offset: TripleAsArray<Int, Int, Int> = TripleAsArray(0, 0, 0),
	var state: BlockState = blockStateStone(),
) : BlockPredicate()

fun wouldSurvive(offset: TripleAsArray<Int, Int, Int> = TripleAsArray(0, 0, 0), state: BlockState = blockStateStone()) =
	WouldSurvive(offset, state)

fun MutableList<BlockPredicate>.wouldSurvive(
	offset: TripleAsArray<Int, Int, Int> = TripleAsArray(0, 0, 0),
	state: BlockState = blockStateStone(),
) {
	this += WouldSurvive(offset, state)
}
