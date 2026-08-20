package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.block.blockStateStone
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicate
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicateScope
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicatesScope
import io.github.ayfri.kore.features.worldgen.blockpredicate.True
import io.github.ayfri.kore.features.worldgen.blockpredicate.blockPredicate
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import kotlinx.serialization.Serializable

@Serializable
data class Lake(
	var fluid: BlockState = blockStateStone(),
	var barrier: BlockState = blockStateStone(),
	var canPlaceFeature: BlockPredicate = True,
	var canReplaceWithAirOrFluid: BlockPredicate = True,
	var canReplaceWithBarrier: BlockPredicate = True,
) : FeatureConfig(), BlockPredicateScope

/**
 * Creates a `lake` configured feature, carving a pocket of [fluid] surrounded by [barrier].
 *
 * The block predicate builders are scoped to [block].
 *
 * ```kotlin
 * lake("lava_lake", fluid = blockState(Blocks.LAVA), barrier = blockState(Blocks.STONE)) {
 *     canPlaceFeature { solid() }
 *     canReplaceWithAirOrFluid { replaceable() }
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/configured_feature/<fileName>.json`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#lake
 */
fun ConfiguredFeatures.lake(
	fileName: String,
	fluid: BlockState = blockStateStone(),
	barrier: BlockState = blockStateStone(),
	block: Lake.() -> Unit = {},
): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, Lake(fluid, barrier).apply(block))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}

/**
 * Sets [Lake.canPlaceFeature] to the predicate built in [block], the blocks the lake may be carved into.
 *
 * A lone predicate is used as-is, several of them are wrapped in an `all_of`.
 *
 * ```kotlin
 * lake("lava_lake", fluid = blockState(Blocks.LAVA)) {
 *     canPlaceFeature { solid() }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate
 */
fun Lake.canPlaceFeature(block: BlockPredicatesScope.() -> Unit) {
	canPlaceFeature = blockPredicate(block)
}

/**
 * Sets [Lake.canReplaceWithAirOrFluid] to the predicate built in [block], the blocks replaced by the fluid or by air.
 *
 * A lone predicate is used as-is, several of them are wrapped in an `all_of`.
 *
 * ```kotlin
 * lake("lava_lake", fluid = blockState(Blocks.LAVA)) {
 *     canReplaceWithAirOrFluid { replaceable() }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate
 */
fun Lake.canReplaceWithAirOrFluid(block: BlockPredicatesScope.() -> Unit) {
	canReplaceWithAirOrFluid = blockPredicate(block)
}

/**
 * Sets [Lake.canReplaceWithBarrier] to the predicate built in [block], the blocks replaced by the barrier block.
 *
 * A lone predicate is used as-is, several of them are wrapped in an `all_of`.
 *
 * ```kotlin
 * lake("lava_lake", fluid = blockState(Blocks.LAVA)) {
 *     canReplaceWithBarrier { matchingBlocks(Blocks.DIRT) }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate
 */
fun Lake.canReplaceWithBarrier(block: BlockPredicatesScope.() -> Unit) {
	canReplaceWithBarrier = blockPredicate(block)
}
