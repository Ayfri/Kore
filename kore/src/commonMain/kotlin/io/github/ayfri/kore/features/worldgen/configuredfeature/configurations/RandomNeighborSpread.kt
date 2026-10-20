package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.arguments.types.BlockOrTagArgument
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicate
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicateScope
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicatesScope
import io.github.ayfri.kore.features.worldgen.blockpredicate.True
import io.github.ayfri.kore.features.worldgen.blockpredicate.blockPredicate
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProviderScope
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.SimpleStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProviderScope
import io.github.ayfri.kore.serializers.InlinableList
import io.github.ayfri.kore.serializers.JsonSerialName
import kotlinx.serialization.Serializable

/**
 * Configuration for the `random_neighbor_spread` feature.
 *
 * Picks random positions around the placement position and places [block] on the replaceable ones that touch one of
 * the [acceptedNeighbors], so a blob grows outwards from the blocks it is attached to. Vanilla grows the extra
 * glowstone around the glowstone blobs of the Nether with it.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#random_neighbor_spread
 *
 * @property acceptedNeighbors A block, a block tag or a list of blocks one of which must touch the position.
 * @property attempts Number of random positions tried, `1` to `3000`.
 * @property block The block state provider of the placed blocks.
 * @property canReplace The blocks that can be replaced.
 * @property xzOffset Horizontal range of the random offset applied to every attempt, `-16` to `16`.
 * Serialized as `xy_offset`.
 * @property yOffset Vertical range of the random offset applied to every attempt, `-16` to `16`.
 */
@Serializable
data class RandomNeighborSpread(
	var acceptedNeighbors: InlinableList<BlockOrTagArgument> = emptyList(),
	var attempts: IntProvider = ConstantIntProvider(1),
	var block: BlockStateProvider = SimpleStateProvider(),
	var canReplace: BlockPredicate = True,
	/** Written `xy_offset`, a 26.3-snapshot-2 typo fixed to `xz_offset` in a later snapshot. */
	@JsonSerialName("xy_offset")
	var xzOffset: IntProvider = ConstantIntProvider(0),
	var yOffset: IntProvider = ConstantIntProvider(0),
) : FeatureConfig(), BlockPredicateScope, BlockStateProviderScope, IntProviderScope

/**
 * Creates a `random_neighbor_spread` configured feature, configured in [block].
 *
 * The block state provider, int provider and block predicate builders are scoped to [block].
 *
 * ```kotlin
 * randomNeighborSpread("glowstone_extra") {
 *     block = simpleStateProvider(Blocks.GLOWSTONE)
 *     acceptedNeighbors(Blocks.GLOWSTONE)
 *     canReplace { matchingBlocks(Blocks.AIR) }
 *     attempts = constant(1500)
 *     xzOffset = trapezoid(-7, 7, 0)
 *     yOffset = uniform(-11, 0)
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#random_neighbor_spread
 */
fun ConfiguredFeatures.randomNeighborSpread(fileName: String, block: RandomNeighborSpread.() -> Unit = {}) =
	dp.configuredFeature(fileName, RandomNeighborSpread().apply(block))

/**
 * Sets [RandomNeighborSpread.acceptedNeighbors] to [blocks], the blocks (or block tags) one of which must touch the
 * position.
 *
 * ```kotlin
 * randomNeighborSpread("glowstone_extra") {
 *     acceptedNeighbors(Blocks.GLOWSTONE, Blocks.NETHERRACK)
 * }
 * ```
 */
fun RandomNeighborSpread.acceptedNeighbors(vararg blocks: BlockOrTagArgument) {
	acceptedNeighbors = blocks.toList()
}

/**
 * Sets [RandomNeighborSpread.canReplace] to the predicate built in [block], the blocks that can be replaced.
 *
 * A lone predicate is used as-is, several of them are wrapped in an `all_of`.
 *
 * ```kotlin
 * randomNeighborSpread("glowstone_extra") {
 *     canReplace { matchingBlocks(Blocks.AIR) }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate
 */
fun RandomNeighborSpread.canReplace(block: BlockPredicatesScope.() -> Unit) {
	canReplace = blockPredicate(block)
}
