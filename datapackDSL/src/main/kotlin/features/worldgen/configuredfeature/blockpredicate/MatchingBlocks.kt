package features.worldgen.configuredfeature.blockpredicate

import arguments.types.BlockOrTagArgument
import serializers.InlinableList
import serializers.TripleAsArray
import kotlinx.serialization.Serializable

@Serializable
data class MatchingBlocks(
	var offset: TripleAsArray<Int, Int, Int> = TripleAsArray(0, 0, 0),
	var blocks: InlinableList<BlockOrTagArgument> = emptyList(),
) : BlockPredicate()

fun MutableList<BlockPredicate>.matchingBlocks(
	offset: TripleAsArray<Int, Int, Int> = TripleAsArray(0, 0, 0),
	blocks: InlinableList<BlockOrTagArgument> = emptyList(),
) {
	this += MatchingBlocks(offset, blocks)
}

fun MutableList<BlockPredicate>.matchingBlocks(
	offset: TripleAsArray<Int, Int, Int> = TripleAsArray(0, 0, 0),
	vararg blocks: BlockOrTagArgument,
) {
	this += MatchingBlocks(offset, blocks.toList())
}

fun matchingBlocks(
	offset: TripleAsArray<Int, Int, Int> = TripleAsArray(0, 0, 0),
	blocks: InlinableList<BlockOrTagArgument> = emptyList(),
) = MatchingBlocks(offset, blocks)

fun matchingBlocks(
	offset: TripleAsArray<Int, Int, Int> = TripleAsArray(0, 0, 0),
	vararg blocks: BlockOrTagArgument,
) = MatchingBlocks(offset, blocks.toList())
