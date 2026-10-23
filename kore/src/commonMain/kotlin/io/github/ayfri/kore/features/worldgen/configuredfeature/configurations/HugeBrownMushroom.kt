package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicate
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicateScope
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicatesScope
import io.github.ayfri.kore.features.worldgen.blockpredicate.True
import io.github.ayfri.kore.features.worldgen.blockpredicate.blockPredicate
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProviderScope
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.SimpleBlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import kotlinx.serialization.Serializable

/**
 * Configuration for the `huge_brown_mushroom` feature.
 *
 * Grows a huge brown mushroom, a flat wide cap on a stem. Vanilla uses it in the mushroom fields and the dark forest.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#huge_brown_mushroom
 *
 * @property canPlaceOn The ground blocks the mushroom may grow on.
 * @property capProvider The block states making up the cap.
 * @property stemProvider The block states making up the stem.
 * @property foliageRadius The horizontal radius of the cap, `2` when `null`, `3` in vanilla.
 */
@Serializable
data class HugeBrownMushroom(
	var canPlaceOn: BlockPredicate = True,
	var capProvider: BlockStateProvider = SimpleBlockStateProvider(),
	var stemProvider: BlockStateProvider = SimpleBlockStateProvider(),
	var foliageRadius: Int? = null,
) : FeatureConfig(), BlockPredicateScope, BlockStateProviderScope

/**
 * Creates a `huge_brown_mushroom` configured feature.
 *
 * The block predicate and block state provider builders are scoped to [block].
 *
 * ```kotlin
 * hugeBrownMushroom("brown_mushroom") {
 *     canPlaceOn { matchingBlockTag(Tags.Block.HUGE_BROWN_MUSHROOM_CAN_PLACE_ON) }
 *     capProvider = simpleStateProvider(Blocks.BROWN_MUSHROOM_BLOCK)
 *     stemProvider = simpleStateProvider(Blocks.MUSHROOM_STEM)
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#huge_brown_mushroom
 */
fun ConfiguredFeatures.hugeBrownMushroom(
	fileName: String,
	foliageRadius: Int? = null,
	block: HugeBrownMushroom.() -> Unit = {},
) = dp.configuredFeature(fileName, HugeBrownMushroom(foliageRadius = foliageRadius).apply(block))

/**
 * Sets [HugeBrownMushroom.canPlaceOn] to the predicate built in [block], the ground blocks the mushroom may grow on.
 *
 * A lone predicate is used as-is, several of them are wrapped in an `all_of`.
 *
 * ```kotlin
 * hugeBrownMushroom("brown_mushroom") {
 *     canPlaceOn { matchingBlockTag(Tags.Block.HUGE_BROWN_MUSHROOM_CAN_PLACE_ON) }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate
 */
fun HugeBrownMushroom.canPlaceOn(block: BlockPredicatesScope.() -> Unit) {
	canPlaceOn = blockPredicate(block)
}
