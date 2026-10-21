package io.github.ayfri.kore.features.predicates.conditions

import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.features.predicates.Predicate
import io.github.ayfri.kore.features.predicates.sub.BlockPredicate
import io.github.ayfri.kore.features.predicates.sub.blockPredicate
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

/**
 * Passes when the block of the loot context matches [predicate], written flat next to the `type` key:
 * `{ "type": "minecraft:match_block", "blocks": "minecraft:wheat", "state": { "age": "7" } }`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/predicates
 * Minecraft Wiki: [Predicate - match_block](https://minecraft.wiki/w/Predicate#match_block)
 */
@Serializable(with = MatchBlock.Companion.MatchBlockSerializer::class)
data class MatchBlock(var predicate: BlockPredicate = BlockPredicate()) : PredicateCondition() {
	companion object {
		data object MatchBlockSerializer : InlineAutoSerializer<MatchBlock, BlockPredicate>(
			BlockPredicate.serializer(),
			MatchBlock::predicate,
			::MatchBlock,
			"match_block",
		)
	}
}

/**
 * Adds a [MatchBlock] condition passing when the block is any of [blocks] and matches the checks of [block].
 *
 * ```
 * matchBlock(Blocks.WHEAT) { state("age", "7") }  // "blocks": "minecraft:wheat", "state": { "age": "7" }
 * matchBlock(Tags.Block.LOGS)                     // "blocks": "#minecraft:logs"
 * ```
 */
fun Predicate.matchBlock(vararg blocks: BlockOrTagArgument, block: BlockPredicate.() -> Unit = {}) {
	predicateConditions += MatchBlock(blockPredicate(*blocks, init = block))
}
