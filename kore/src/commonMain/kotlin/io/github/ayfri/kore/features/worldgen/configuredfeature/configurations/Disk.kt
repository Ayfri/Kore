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
import io.github.ayfri.kore.features.worldgen.intproviders.constant
import kotlinx.serialization.Serializable

/**
 * Configuration for the `disk` feature.
 *
 * Replaces the blocks matching [target] in a flat horizontal disk of the placement position. Vanilla makes the clay,
 * gravel, sand and grass disks and the ice patches of the ice spikes with it.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#disk
 *
 * @property stateProvider The block states placed inside the disk.
 * @property target The predicate the replaced blocks have to pass.
 * @property radius Horizontal radius of the disk, `0` to `8`.
 * @property halfHeight Half of the height of the disk, `0` to `4`: the amount of blocks replaced above and below the
 * center layer.
 */
@Serializable
data class Disk(
	var stateProvider: BlockStateProvider = SimpleStateProvider(),
	var target: BlockPredicate = True,
	var radius: IntProvider = ConstantIntProvider(0),
	var halfHeight: Int = 0,
) : FeatureConfig(), BlockPredicateScope, BlockStateProviderScope, IntProviderScope

/**
 * Creates a `disk` configured feature, replacing the blocks matching [Disk.target] by [Disk.stateProvider].
 *
 * The block predicate and block state provider builders are scoped to [block].
 *
 * ```kotlin
 * disk("clay_disk", radius = uniform(2, 3), halfHeight = 1) {
 *     stateProvider = simpleStateProvider(Blocks.CLAY)
 *     target { matchingBlocks(Blocks.DIRT, Blocks.CLAY) }
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#disk
 */
fun ConfiguredFeatures.disk(
	fileName: String,
	radius: IntProvider = ConstantIntProvider(0),
	halfHeight: Int = 0,
	block: Disk.() -> Unit = {},
) = dp.configuredFeature(fileName, Disk(radius = radius, halfHeight = halfHeight).apply(block))

/**
 * Creates a `disk` configured feature with a constant [radius].
 *
 * ```kotlin
 * disk("clay_disk", radius = 3, halfHeight = 1) {
 *     stateProvider = simpleStateProvider(Blocks.CLAY)
 * }
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#disk
 */
fun ConfiguredFeatures.disk(
	fileName: String,
	radius: Int,
	halfHeight: Int = 0,
	block: Disk.() -> Unit = {},
) = disk(fileName, constant(radius), halfHeight, block)

/**
 * Sets [Disk.target] to the predicate built in [block], the blocks the disk replaces.
 *
 * A lone predicate is used as-is, several of them are wrapped in an `all_of`.
 *
 * ```kotlin
 * disk("clay_disk") {
 *     stateProvider = simpleStateProvider(Blocks.CLAY)
 *     target { matchingBlocks(Blocks.DIRT, Blocks.CLAY) }
 * }
 * ```
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Block_predicate
 */
fun Disk.target(block: BlockPredicatesScope.() -> Unit) {
	target = blockPredicate(block)
}
