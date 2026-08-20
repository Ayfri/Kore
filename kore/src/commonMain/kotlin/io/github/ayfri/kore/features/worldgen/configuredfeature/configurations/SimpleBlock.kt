package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProvider
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.BlockStateProviderScope
import io.github.ayfri.kore.features.worldgen.configuredfeature.blockstateprovider.SimpleStateProvider
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import kotlinx.serialization.Serializable

/**
 * Configuration for the `simple_block` feature, placing a single block at the position, the base of most of the
 * vanilla vegetation features.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#simple_block
 *
 * @property toPlace The block state placed at the position.
 * @property scheduleTick Whether the placed block gets a block tick scheduled, updating right after placement.
 */
@Serializable
data class SimpleBlock(
	var toPlace: BlockStateProvider = SimpleStateProvider(),
	var scheduleTick: Boolean = false,
) : FeatureConfig(), BlockStateProviderScope

/**
 * Creates a `simple_block` configured feature, placing the block given by [SimpleBlock.toPlace].
 *
 * The block state provider builders are scoped to [block].
 *
 * ```kotlin
 * simpleBlock("dandelion") {
 *     toPlace = simpleStateProvider(Blocks.DANDELION)
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/configured_feature/<fileName>.json`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#simple_block
 */
fun ConfiguredFeatures.simpleBlock(
	fileName: String,
	scheduleTick: Boolean = false,
	block: SimpleBlock.() -> Unit = {},
): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, SimpleBlock(scheduleTick = scheduleTick).apply(block))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}
