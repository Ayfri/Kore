package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.Target
import io.github.ayfri.kore.features.worldgen.configuredfeature.TargetsHolder
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import kotlinx.serialization.Serializable

/**
 * Places a vein of blocks replacing the terrain, the classic ore generation.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Ore_(feature)
 *
 * @property size The maximum amount of blocks of a vein.
 * @property discardChanceOnAirExposure The chance to skip a block of the vein exposed to air, between `0.0` and `1.0`.
 * @property targets The replacement targets, the first matching one wins.
 */
@Serializable
data class Ore(
	var size: Int = 0,
	var discardChanceOnAirExposure: Double = 0.0,
	override var targets: List<Target> = emptyList(),
) : FeatureConfig(), TargetsHolder

/**
 * Creates an `ore` configured feature, placing veins of up to [size] blocks.
 *
 * ```kotlin
 * ore("iron_ore", size = 9) {
 *     targets {
 *         target(blockState(Blocks.IRON_ORE)) {
 *             target = tagMatch(Tags.Block.STONE_ORE_REPLACEABLES)
 *         }
 *     }
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/configured_feature/<fileName>.json`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Ore_(feature)
 */
fun ConfiguredFeatures.ore(
	fileName: String,
	size: Int = 0,
	discardChanceOnAirExposure: Double = 0.0,
	block: Ore.() -> Unit = {},
): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, Ore(size, discardChanceOnAirExposure).apply(block))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}
