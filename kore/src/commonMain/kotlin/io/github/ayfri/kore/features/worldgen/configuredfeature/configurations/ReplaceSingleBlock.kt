package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.Target
import io.github.ayfri.kore.features.worldgen.configuredfeature.TargetsHolder
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import kotlinx.serialization.Serializable

/**
 * Replaces the single block at the placement position, when it matches one of the targets, used by the emerald ore.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature
 *
 * @property targets The replacement targets, the first matching one wins.
 */
@Serializable
data class ReplaceSingleBlock(
	override var targets: List<Target> = emptyList(),
) : FeatureConfig(), TargetsHolder

/**
 * Creates a `replace_single_block` configured feature, replacing one block when it matches one of the targets.
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
 * Produces `data/<namespace>/worldgen/configured_feature/<fileName>.json`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature
 */
fun ConfiguredFeatures.replaceSingleBlock(
	fileName: String,
	block: ReplaceSingleBlock.() -> Unit = {},
): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, ReplaceSingleBlock().apply(block))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}
