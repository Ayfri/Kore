package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.Target
import io.github.ayfri.kore.features.worldgen.configuredfeature.TargetsHolder
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import kotlinx.serialization.Serializable

/**
 * Places a vein of blocks replacing the terrain, spreading the blocks around instead of packing them, used by the
 * nether ores.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature
 *
 * @property size The maximum amount of blocks of a vein.
 * @property discardChanceOnAirExposure The chance to skip a block of the vein exposed to air, between `0.0` and `1.0`.
 * @property targets The replacement targets, the first matching one wins.
 */
@Serializable
data class ScatteredOre(
	var size: Int = 0,
	var discardChanceOnAirExposure: Double = 0.0,
	override var targets: List<Target> = emptyList(),
) : FeatureConfig(), TargetsHolder

/**
 * Creates a `scattered_ore` configured feature, spreading veins of up to [size] blocks.
 *
 * ```kotlin
 * scatteredOre("nether_gold", size = 10) {
 *     targets {
 *         target(blockState(Blocks.NETHER_GOLD_ORE)) {
 *             target = blockMatch(Blocks.NETHERRACK)
 *         }
 *     }
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/configured_feature/<fileName>.json`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature
 */
fun ConfiguredFeatures.scatteredOre(
	fileName: String,
	size: Int = 0,
	discardChanceOnAirExposure: Double = 0.0,
	block: ScatteredOre.() -> Unit = {},
): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, ScatteredOre(size, discardChanceOnAirExposure).apply(block))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}
