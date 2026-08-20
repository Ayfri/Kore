package io.github.ayfri.kore.features.worldgen.blockpredicate

import io.github.ayfri.kore.arguments.types.TaggedResourceLocationArgument
import io.github.ayfri.kore.arguments.types.resources.tagged.BlockTagArgument
import io.github.ayfri.kore.serializers.TripleAsArray
import kotlinx.serialization.Serializable

/**
 * Passes when the block at [offset] is in [tag].
 *
 * The tag is written without its `#` prefix, as Minecraft expects it here.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate#matching_block_tag
 *
 * @property offset The `[X, Y, Z]` block offset to test at, each component between `-16` and `16`, `[0, 0, 0]` when `null`.
 * @property tag The block tag to match.
 */
@Serializable
data class MatchingBlockTag(
	override var offset: TripleAsArray<Int, Int, Int>? = null,
	@Serializable(TaggedResourceLocationArgument.TaggedResourceLocationUnPrefixedSerializer::class)
	var tag: BlockTagArgument,
) : BlockPredicate(), OffsetBlockPredicate

/**
 * Creates a `matching_block_tag` block predicate, passing when the tested block is in [tag].
 *
 * ```kotlin
 * blockPredicateFilter {
 *     predicate { matchingBlockTag(Tags.Block.DIRT) { offset(0, -1, 0) } }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate#matching_block_tag
 */
fun BlockPredicateScope.matchingBlockTag(tag: BlockTagArgument, init: MatchingBlockTag.() -> Unit = {}) =
	MatchingBlockTag(tag = tag).apply(init).also { addBlockPredicate(it) }
