package io.github.ayfri.kore.features.worldgen.blockpredicate

import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.block.blockStateStone
import io.github.ayfri.kore.serializers.TripleAsArray
import kotlinx.serialization.Serializable

/**
 * Passes when [state] is a valid placement at [offset], as if the block was placed by a player.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate#would_survive
 *
 * @property offset The `[X, Y, Z]` block offset to test at, each component between `-16` and `16`, `[0, 0, 0]` when `null`.
 * @property state The block state that has to survive.
 */
@Serializable
data class WouldSurvive(
	override var offset: TripleAsArray<Int, Int, Int>? = null,
	var state: BlockState = blockStateStone(),
) : BlockPredicate(), OffsetBlockPredicate

/**
 * Creates a `would_survive` block predicate, passing when [state] is a valid placement at the tested position.
 *
 * ```kotlin
 * blockPredicateFilter {
 *     predicate { wouldSurvive(blockState(Blocks.OAK_SAPLING)) { offset(0, 1, 0) } }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate#would_survive
 */
fun BlockPredicateScope.wouldSurvive(state: BlockState, init: WouldSurvive.() -> Unit = {}) =
	WouldSurvive(state = state).apply(init).also { addBlockPredicate(it) }
