package io.github.ayfri.kore.features.worldgen.blockpredicate

import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.serializers.InlinableList
import io.github.ayfri.kore.serializers.TripleAsArray
import kotlinx.serialization.Serializable

@Serializable
data class MatchingBlocks(
	var offset: TripleAsArray<Int, Int, Int>? = null,
	var blocks: InlinableList<BlockOrTagArgument> = emptyList(),
) : BlockPredicate()

fun MutableList<BlockPredicate>.matchingBlocks(
	offset: TripleAsArray<Int, Int, Int>? = null,
	blocks: InlinableList<BlockOrTagArgument> = emptyList(),
) {
	this += MatchingBlocks(offset, blocks)
}

fun MutableList<BlockPredicate>.matchingBlocks(offset: TripleAsArray<Int, Int, Int>? = null, vararg blocks: BlockOrTagArgument) {
	this += MatchingBlocks(offset, blocks.toList())
}

fun matchingBlocks(
	offset: TripleAsArray<Int, Int, Int>? = null,
	blocks: InlinableList<BlockOrTagArgument> = emptyList(),
) = MatchingBlocks(offset, blocks)

fun matchingBlocks(
	offset: TripleAsArray<Int, Int, Int>? = null,
	vararg blocks: BlockOrTagArgument,
) = MatchingBlocks(offset, blocks.toList())
