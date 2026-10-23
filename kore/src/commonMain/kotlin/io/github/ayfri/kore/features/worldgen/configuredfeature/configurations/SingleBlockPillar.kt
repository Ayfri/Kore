package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicate
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicateScope
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicatesScope
import io.github.ayfri.kore.features.worldgen.blockpredicate.blockPredicate
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.VerticalDirection
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProviderScope
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.SimpleBlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import io.github.ayfri.kore.generated.arguments.worldgen.types.PlacedFeatureArgument
import kotlinx.serialization.Serializable

/**
 * Configuration for the `single_block_pillar` feature.
 *
 * Stacks one block after the other up or down from the placement position. Vanilla hangs the basalt pillars of the
 * basalt deltas from the ceiling with it, finishing them with a [capFeature].
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#single_block_pillar
 *
 * @property block The block state provider of the pillar.
 * @property canReplace The blocks the pillar can grow into, every block when `null`.
 * @property capFeature The placed feature run at the end of the pillar, none when `null`.
 * @property chanceToContinue Between `0` and `1`, the chance of growing one more block after each placed block, `1` when `null`.
 * @property direction Whether the pillar grows [VerticalDirection.UP] or [VerticalDirection.DOWN].
 */
@Serializable
data class SingleBlockPillar(
	var block: BlockStateProvider = SimpleBlockStateProvider(),
	var canReplace: BlockPredicate? = null,
	var capFeature: PlacedFeatureArgument? = null,
	var chanceToContinue: Float? = null,
	var direction: VerticalDirection = VerticalDirection.UP,
) : FeatureConfig(), BlockPredicateScope, BlockStateProviderScope

/**
 * Creates a `single_block_pillar` configured feature growing in [direction], configured in [block].
 *
 * The block state provider and block predicate builders are scoped to [block].
 *
 * ```kotlin
 * singleBlockPillar("basalt_pillar", VerticalDirection.DOWN) {
 *     block = simpleStateProvider(Blocks.BASALT)
 *     canReplace { matchingBlocks(Blocks.AIR) }
 *     chanceToContinue = 0.9f
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#single_block_pillar
 */
fun ConfiguredFeatures.singleBlockPillar(
	fileName: String,
	direction: VerticalDirection = VerticalDirection.UP,
	block: SingleBlockPillar.() -> Unit = {},
) = dp.configuredFeature(fileName, SingleBlockPillar(direction = direction).apply(block))

/**
 * Sets [SingleBlockPillar.canReplace] to the predicate built in [block], the blocks the pillar can grow into.
 *
 * A lone predicate is used as-is, several of them are wrapped in an `all_of`.
 *
 * ```kotlin
 * singleBlockPillar("basalt_pillar") {
 *     canReplace { matchingBlocks(Blocks.AIR) }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate
 */
fun SingleBlockPillar.canReplace(block: BlockPredicatesScope.() -> Unit) {
	canReplace = blockPredicate(block)
}
