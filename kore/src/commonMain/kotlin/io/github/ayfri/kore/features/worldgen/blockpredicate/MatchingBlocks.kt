package io.github.ayfri.kore.features.worldgen.blockpredicate

import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.serializers.InlinableList
import io.github.ayfri.kore.serializers.TripleAsArray
import kotlinx.serialization.Serializable

/**
 * Passes when the block at [offset] is one of [blocks], given as a single block, a list of blocks or a block tag.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate#matching_blocks
 *
 * @property offset The `[X, Y, Z]` block offset to test at, each component between `-16` and `16`, `[0, 0, 0]` when `null`.
 * @property blocks The blocks to match, serialized as a bare string when there is exactly one.
 */
@Serializable
data class MatchingBlocks(
	override var offset: TripleAsArray<Int, Int, Int>? = null,
	var blocks: InlinableList<BlockOrTagArgument> = emptyList(),
) : BlockPredicate(), OffsetBlockPredicate

/**
 * Creates a `matching_blocks` block predicate, passing when the tested block is one of [blocks].
 *
 * ```kotlin
 * blockPredicateFilter {
 *     predicate { matchingBlocks(Blocks.STONE, Blocks.DEEPSLATE) { offset(0, -1, 0) } }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate#matching_blocks
 */
fun BlockPredicateScope.matchingBlocks(vararg blocks: BlockOrTagArgument, init: MatchingBlocks.() -> Unit = {}) =
	MatchingBlocks(blocks = blocks.toList()).apply(init).also { addBlockPredicate(it) }

/**
 * Creates a `matching_blocks` block predicate, passing when the tested block is one of [blocks].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate#matching_blocks
 */
fun BlockPredicateScope.matchingBlocks(
	blocks: InlinableList<BlockOrTagArgument>,
	init: MatchingBlocks.() -> Unit = {},
) = MatchingBlocks(blocks = blocks).apply(init).also { addBlockPredicate(it) }
