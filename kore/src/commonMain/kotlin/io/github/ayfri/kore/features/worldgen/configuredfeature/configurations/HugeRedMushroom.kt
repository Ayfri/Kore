package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicate
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicateScope
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicatesScope
import io.github.ayfri.kore.features.worldgen.blockpredicate.True
import io.github.ayfri.kore.features.worldgen.blockpredicate.blockPredicate
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.simpleStateProvider
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import kotlinx.serialization.Serializable

/**
 * Configuration for the `huge_red_mushroom` feature.
 *
 * [canPlaceOn] controls which ground blocks the mushroom may grow on.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#huge_red_mushroom
 */
@Serializable
data class HugeRedMushroom(
	var canPlaceOn: BlockPredicate = True,
	var capProvider: BlockStateProvider = simpleStateProvider(),
	var stemProvider: BlockStateProvider = simpleStateProvider(),
	var foliageRadius: Int? = null,
) : FeatureConfig(), BlockPredicateScope

/**
 * Creates a `huge_red_mushroom` configured feature.
 *
 * The block predicate builders are scoped to [block].
 *
 * ```kotlin
 * hugeRedMushroom("red_mushroom") {
 *     canPlaceOn { matchingBlockTag(Tags.Block.MUSHROOM_GROW_BLOCK) }
 *     capProvider = simpleStateProvider(Blocks.RED_MUSHROOM_BLOCK)
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/configured_feature/<fileName>.json`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#huge_red_mushroom
 */
fun ConfiguredFeatures.hugeRedMushroom(
	fileName: String,
	capProvider: BlockStateProvider = simpleStateProvider(),
	stemProvider: BlockStateProvider = simpleStateProvider(),
	foliageRadius: Int? = null,
	block: HugeRedMushroom.() -> Unit = {},
): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(
		fileName,
		HugeRedMushroom(capProvider = capProvider, stemProvider = stemProvider, foliageRadius = foliageRadius).apply(block),
	)
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
