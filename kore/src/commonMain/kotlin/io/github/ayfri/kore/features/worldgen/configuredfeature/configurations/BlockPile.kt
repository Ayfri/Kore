package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProviderScope
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.SimpleStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import kotlinx.serialization.Serializable

/**
 * Configuration for the `block_pile` feature.
 *
 * Places a small pile of blocks. Vanilla uses it for the hay, melon, pumpkin, snow and ice piles.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#block_pile
 *
 * @property stateProvider The block states making up the pile.
 */
@Serializable
data class BlockPile(
	var stateProvider: BlockStateProvider = SimpleStateProvider(),
) : FeatureConfig(), BlockStateProviderScope

/**
 * Creates a `block_pile` configured feature, piling up the blocks given by [BlockPile.stateProvider].
 *
 * The block state provider builders are scoped to [block].
 *
 * ```kotlin
 * blockPile("hay_pile") {
 *     stateProvider = rotatedBlockProvider(Blocks.HAY_BLOCK)
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#block_pile
 */
fun ConfiguredFeatures.blockPile(fileName: String, block: BlockPile.() -> Unit = {}) =
	dp.configuredFeature(fileName, BlockPile().apply(block))
