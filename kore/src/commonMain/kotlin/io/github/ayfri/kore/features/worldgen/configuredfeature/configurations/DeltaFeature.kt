package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.data.block.BlockState
import io.github.ayfri.kore.data.block.blockStateStone
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import io.github.ayfri.kore.features.worldgen.intproviders.ConstantIntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProvider
import io.github.ayfri.kore.features.worldgen.intproviders.IntProviderScope
import io.github.ayfri.kore.features.worldgen.intproviders.constant
import kotlinx.serialization.Serializable

/**
 * Configuration for the `delta_feature` feature.
 *
 * Fills a pool at the placement position with [contents], ringed by a rim of [rim]. It only replaces blocks that have
 * air above and no air beside them, so it never generates underwater. Vanilla makes the lava pools of the basalt
 * deltas, rimmed with magma blocks, with it.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#delta_feature
 *
 * @property contents The block state filling the inside of the pool.
 * @property rim The block state of the rim around the pool.
 * @property size Size of the inside of the pool, `0` to `16`.
 * @property rimSize Size of the rim around the pool, `0` to `16`.
 */
@Serializable
data class DeltaFeature(
	var contents: BlockState = blockStateStone(),
	var rim: BlockState = blockStateStone(),
	var size: IntProvider = ConstantIntProvider(0),
	var rimSize: IntProvider = ConstantIntProvider(0),
) : FeatureConfig(), IntProviderScope

/**
 * Creates a `delta_feature` configured feature with a [contents] pool ringed by [rim], their sizes being int providers.
 *
 * The int provider builders are scoped to [block].
 *
 * ```kotlin
 * deltaFeature("lava_delta", contents = blockState(Blocks.LAVA), rim = blockState(Blocks.MAGMA_BLOCK)) {
 *     size = uniform(3, 7)
 *     rimSize = uniform(0, 2)
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#delta_feature
 */
fun ConfiguredFeatures.deltaFeature(
	fileName: String,
	contents: BlockState = blockStateStone(),
	rim: BlockState = blockStateStone(),
	size: IntProvider = ConstantIntProvider(0),
	rimSize: IntProvider = ConstantIntProvider(0),
	block: DeltaFeature.() -> Unit = {},
) = dp.configuredFeature(fileName, DeltaFeature(contents, rim, size, rimSize).apply(block))

/**
 * Creates a `delta_feature` configured feature with a constant [size] and [rimSize].
 *
 * ```kotlin
 * deltaFeature("lava_delta", contents = blockState(Blocks.LAVA), rim = blockState(Blocks.MAGMA_BLOCK), size = 5, rimSize = 1)
 * ```
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#delta_feature
 */
fun ConfiguredFeatures.deltaFeature(
	fileName: String,
	contents: BlockState = blockStateStone(),
	rim: BlockState = blockStateStone(),
	size: Int,
	rimSize: Int,
) = dp.configuredFeature(fileName, DeltaFeature(contents, rim, constant(size), constant(rimSize)))
