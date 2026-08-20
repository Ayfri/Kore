package io.github.ayfri.kore.features.worldgen.placedfeature.modifiers

import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicate
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicateScope
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicatesScope
import io.github.ayfri.kore.features.worldgen.blockpredicate.True
import io.github.ayfri.kore.features.worldgen.blockpredicate.blockPredicate
import io.github.ayfri.kore.features.worldgen.placedfeature.PlacedFeature
import kotlinx.serialization.Serializable

/**
 * Placement modifier discarding the positions failing [predicate].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Placed_feature#block_predicate_filter
 *
 * @property predicate The condition a position has to pass to be kept.
 */
@Serializable
data class BlockPredicateFilter(
	var predicate: BlockPredicate = True,
) : PlacementModifier(), BlockPredicateScope

/**
 * Appends a `block_predicate_filter` placement modifier, discarding the positions failing
 * [BlockPredicateFilter.predicate].
 *
 * The block predicate builders are scoped to [block].
 *
 * ```kotlin
 * placedFeature("my_feature", ConfiguredFeatures.ACACIA) {
 *     blockPredicateFilter {
 *         predicate { matchingBlockTag(Tags.Block.DIRT) }
 *     }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Placed_feature#block_predicate_filter
 */
fun PlacedFeature.blockPredicateFilter(block: BlockPredicateFilter.() -> Unit = {}) {
	placementModifiers += BlockPredicateFilter().apply(block)
}

/**
 * Sets [BlockPredicateFilter.predicate] to the predicate built in [block], the condition a position has to pass to be
 * kept.
 *
 * A lone predicate is used as-is, several of them are wrapped in an `all_of`.
 *
 * ```kotlin
 * blockPredicateFilter {
 *     predicate { matchingBlockTag(Tags.Block.DIRT) }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate
 */
fun BlockPredicateFilter.predicate(block: BlockPredicatesScope.() -> Unit) {
	predicate = blockPredicate(block)
}
