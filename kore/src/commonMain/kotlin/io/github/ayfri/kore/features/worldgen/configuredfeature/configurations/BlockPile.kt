package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProviderScope
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.SimpleStateProvider
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import kotlinx.serialization.Serializable

/**
 * Configuration for the `block_pile` feature, a small pile of blocks such as the hay and melon piles of the villages.
 *
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
 * Produces `data/<namespace>/worldgen/configured_feature/<fileName>.json`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#block_pile
 */
fun ConfiguredFeatures.blockPile(fileName: String, block: BlockPile.() -> Unit = {}): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, BlockPile().apply(block))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}
