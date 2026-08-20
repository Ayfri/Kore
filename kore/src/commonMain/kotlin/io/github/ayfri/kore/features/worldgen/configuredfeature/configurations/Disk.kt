package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicate
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicateScope
import io.github.ayfri.kore.features.worldgen.blockpredicate.BlockPredicatesScope
import io.github.ayfri.kore.features.worldgen.blockpredicate.True
import io.github.ayfri.kore.features.worldgen.blockpredicate.blockPredicate
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.constant
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import kotlinx.serialization.Serializable

/**
 * Configuration for the `disk` feature, a flat horizontal disk of blocks replacing the terrain, such as the clay and
 * gravel patches of the riverbeds.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#disk
 *
 * @property stateProvider The block states placed inside the disk.
 * @property target The predicate the replaced blocks have to pass.
 * @property radius The horizontal radius of the disk.
 * @property halfHeight The amount of blocks placed above and below the center layer.
 */
@Serializable
data class Disk(
	var stateProvider: BlockStateProvider,
	var target: BlockPredicate = True,
	var radius: IntProvider = constant(0),
	var halfHeight: Int = 0,
) : FeatureConfig(), BlockPredicateScope

/**
 * Creates a `disk` configured feature, replacing the blocks matching [Disk.target] by [stateProvider].
 *
 * The block predicate builders are scoped to [block].
 *
 * ```kotlin
 * disk("clay_disk", stateProvider = simpleStateProvider(Blocks.CLAY), radius = constant(3)) {
 *     target { matchingBlocks(Blocks.DIRT, Blocks.CLAY) }
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/configured_feature/<fileName>.json`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#disk
 */
fun ConfiguredFeatures.disk(
	fileName: String,
	stateProvider: BlockStateProvider,
	radius: IntProvider = constant(0),
	halfHeight: Int = 0,
	block: Disk.() -> Unit = {},
): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, Disk(stateProvider, radius = radius, halfHeight = halfHeight).apply(block))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}

/**
 * Creates a `disk` configured feature with a constant [radius].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#disk
 */
fun ConfiguredFeatures.disk(
	fileName: String,
	stateProvider: BlockStateProvider,
	radius: Int,
	halfHeight: Int = 0,
	block: Disk.() -> Unit = {},
) = disk(fileName, stateProvider, constant(radius), halfHeight, block)

/**
 * Sets [Disk.target] to the predicate built in [block], the blocks the disk replaces.
 *
 * A lone predicate is used as-is, several of them are wrapped in an `all_of`.
 *
 * ```kotlin
 * disk("clay_disk", stateProvider = simpleStateProvider(Blocks.CLAY)) {
 *     target { matchingBlocks(Blocks.DIRT, Blocks.CLAY) }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate
 */
fun Disk.target(block: BlockPredicatesScope.() -> Unit) {
	target = blockPredicate(block)
}
