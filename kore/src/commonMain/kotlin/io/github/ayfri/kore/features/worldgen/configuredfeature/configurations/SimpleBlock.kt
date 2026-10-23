package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProviderScope
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.SimpleBlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import kotlinx.serialization.Serializable

/**
 * Configuration for the `simple_block` feature.
 *
 * Places a single block at the placement position. Vanilla builds most of its flowers, grass, bushes, mushrooms,
 * pumpkins and melons with it, repeated by placement modifiers.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#simple_block
 *
 * @property toPlace The block state provider of the placed block.
 * @property scheduleTick Whether a block update is scheduled for the placed block.
 */
@Serializable
data class SimpleBlock(
	var toPlace: BlockStateProvider = SimpleBlockStateProvider(),
	var scheduleTick: Boolean = false,
) : FeatureConfig(), BlockStateProviderScope

/**
 * Creates a `simple_block` configured feature placing the block given by [SimpleBlock.toPlace], configured in [block].
 *
 * The block state provider builders are scoped to [block].
 *
 * ```kotlin
 * simpleBlock("flower_default") {
 *     toPlace = weightedStateProvider {
 *         entry(Blocks.POPPY, weight = 2)
 *         entry(Blocks.DANDELION)
 *     }
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#simple_block
 */
fun ConfiguredFeatures.simpleBlock(
	fileName: String,
	scheduleTick: Boolean = false,
	block: SimpleBlock.() -> Unit = {},
) = dp.configuredFeature(fileName, SimpleBlock(scheduleTick = scheduleTick).apply(block))
