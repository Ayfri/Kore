package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicate
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicateScope
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicatesScope
import io.github.ayfri.kore.features.worldgen.blockpredicate.True
import io.github.ayfri.kore.features.worldgen.blockpredicate.blockPredicate
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProviderScope
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.SimpleStateProvider
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import kotlinx.serialization.Serializable

/**
 * Configuration for the `huge_red_mushroom` feature.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#huge_red_mushroom
 *
 * @property canPlaceOn The ground blocks the mushroom may grow on.
 * @property capProvider The block states making up the cap.
 * @property stemProvider The block states making up the stem.
 * @property foliageRadius The horizontal radius of the cap, `2` when `null`.
 */
@Serializable
data class HugeRedMushroom(
	var canPlaceOn: BlockPredicate = True,
	var capProvider: BlockStateProvider = SimpleStateProvider(),
	var stemProvider: BlockStateProvider = SimpleStateProvider(),
	var foliageRadius: Int? = null,
) : FeatureConfig(), BlockPredicateScope, BlockStateProviderScope

/**
 * Creates a `huge_red_mushroom` configured feature.
 *
 * The block predicate and block state provider builders are scoped to [block].
 *
 * ```kotlin
 * hugeRedMushroom("red_mushroom") {
 *     canPlaceOn { matchingBlockTag(Tags.Block.MUSHROOM_GROW_BLOCK) }
 *     capProvider = simpleStateProvider(Blocks.RED_MUSHROOM_BLOCK)
 *     stemProvider = simpleStateProvider(Blocks.MUSHROOM_STEM)
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/configured_feature/<fileName>.json`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#huge_red_mushroom
 */
fun ConfiguredFeatures.hugeRedMushroom(
	fileName: String,
	foliageRadius: Int? = null,
	block: HugeRedMushroom.() -> Unit = {},
): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, HugeRedMushroom(foliageRadius = foliageRadius).apply(block))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}

/**
 * Sets [HugeRedMushroom.canPlaceOn] to the predicate built in [block], the ground blocks the mushroom may grow on.
 *
 * A lone predicate is used as-is, several of them are wrapped in an `all_of`.
 *
 * ```kotlin
 * hugeRedMushroom("red_mushroom") {
 *     canPlaceOn { matchingBlockTag(Tags.Block.MUSHROOM_GROW_BLOCK) }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate
 */
fun HugeRedMushroom.canPlaceOn(block: BlockPredicatesScope.() -> Unit) {
	canPlaceOn = blockPredicate(block)
}
