package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

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
import kotlinx.serialization.Serializable

/**
 * Configuration for the `projected_random_patchy_square` feature.
 *
 * Covers a square of side `2 * size + 1` centered on the placement position, keeping each column with a probability that
 * falls off towards the corners (the center cross is always kept). Each kept column is projected downward through the
 * blocks matching [projectThrough], for at most [maxProjectionHeight] steps, and [block] is placed where it stops.
 * Vanilla caps the end of its basalt pillars with it.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#projected_random_patchy_square
 *
 * @property block The block state provider of the placed blocks.
 * @property maxProjectionHeight The most steps down a column is projected, `0` or more, `0` behaving like `1`.
 * @property projectThrough The blocks a projection passes through while moving down.
 * @property size Half the side of the square, `1` to `16`, sampled once per placement.
 */
@Serializable
data class ProjectedRandomPatchySquare(
	var block: BlockStateProvider = SimpleStateProvider(),
	var maxProjectionHeight: Int = 0,
	var projectThrough: BlockPredicate = True,
	var size: IntProvider = ConstantIntProvider(1),
) : FeatureConfig(), BlockPredicateScope, BlockStateProviderScope, IntProviderScope

/**
 * Creates a `projected_random_patchy_square` configured feature, configured in [block].
 *
 * The block state provider, int provider and block predicate builders are scoped to [block].
 *
 * ```kotlin
 * projectedRandomPatchySquare("basalt_cap") {
 *     block = simpleStateProvider(Blocks.BASALT)
 *     projectThrough { matchingBlocks(Blocks.AIR) }
 *     maxProjectionHeight = 3
 *     size = constant(3)
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#projected_random_patchy_square
 */
fun ConfiguredFeatures.projectedRandomPatchySquare(
	fileName: String,
	block: ProjectedRandomPatchySquare.() -> Unit = {},
) = dp.configuredFeature(fileName, ProjectedRandomPatchySquare().apply(block))

/**
 * Sets [ProjectedRandomPatchySquare.projectThrough] to the predicate built in [block], the blocks a projection passes
 * through.
 *
 * A lone predicate is used as-is, several of them are wrapped in an `all_of`.
 *
 * ```kotlin
 * projectedRandomPatchySquare("basalt_cap") {
 *     projectThrough { matchingBlocks(Blocks.AIR) }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate
 */
fun ProjectedRandomPatchySquare.projectThrough(block: BlockPredicatesScope.() -> Unit) {
	projectThrough = blockPredicate(block)
}
