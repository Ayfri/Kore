package io.github.ayfri.kore.features.worldgen.blockpredicate

import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.arguments.types.resources.BlockArgument
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
	blocks: InlinableList<BlockArgument> = emptyList(),
) {
	this += MatchingBlocks(offset, blocks)
}

fun MutableList<BlockPredicate>.matchingBlocks(offset: TripleAsArray<Int, Int, Int>? = null, vararg blocks: BlockArgument) {
	this += MatchingBlocks(offset, blocks.toList())
}

fun MutableList<BlockPredicate>.matchingBlocks(
	offset: TripleAsArray<Int, Int, Int>? = null,
	block: BlockOrTagArgument,
) {
	this += MatchingBlocks(offset, listOf(block))
}

fun matchingBlocks(
	offset: TripleAsArray<Int, Int, Int>? = null,
	blocks: InlinableList<BlockArgument> = emptyList(),
) = MatchingBlocks(offset, blocks)

fun matchingBlocks(
	offset: TripleAsArray<Int, Int, Int>? = null,
	vararg blocks: BlockArgument,
) = MatchingBlocks(offset, blocks.toList())

fun matchingBlocks(
	offset: TripleAsArray<Int, Int, Int>? = null,
	block: BlockOrTagArgument,
) = MatchingBlocks(offset, listOf(block))
