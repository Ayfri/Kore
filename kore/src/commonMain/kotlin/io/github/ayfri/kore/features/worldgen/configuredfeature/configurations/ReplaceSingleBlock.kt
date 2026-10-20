package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.Target
import io.github.ayfri.kore.features.worldgen.configuredfeature.TargetsHolder
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import kotlinx.serialization.Serializable

/**
 * Configuration for the `replace_single_block` feature.
 *
 * Replaces the block at the placement position when it matches one of the targets. Vanilla doesn't use it, its
 * emerald ore is an `ore` feature.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#replace_single_block
 *
 * @property targets The replacement targets, the first matching one wins.
 */
@Serializable
data class ReplaceSingleBlock(
	override var targets: List<Target> = emptyList(),
) : FeatureConfig(), TargetsHolder

/**
 * Creates a `replace_single_block` configured feature replacing one block when it matches one of the targets,
 * configured in [block].
 *
 * ```kotlin
 * replaceSingleBlock("emerald_ore") {
 *     targets {
 *         target(blockState(Blocks.EMERALD_ORE)) {
 *             target = tagMatch(Tags.Block.STONE_ORE_REPLACEABLES)
 *         }
 *     }
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#replace_single_block
 */
fun ConfiguredFeatures.replaceSingleBlock(
	fileName: String,
	block: ReplaceSingleBlock.() -> Unit = {},
) = dp.configuredFeature(fileName, ReplaceSingleBlock().apply(block))
