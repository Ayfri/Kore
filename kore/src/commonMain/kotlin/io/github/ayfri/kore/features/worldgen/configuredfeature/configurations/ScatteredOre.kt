package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.Target
import io.github.ayfri.kore.features.worldgen.configuredfeature.TargetsHolder
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import kotlinx.serialization.Serializable

/**
 * Configuration for the `scattered_ore` feature.
 *
 * Places a vein of blocks replacing the terrain, with the blocks spread around the placement position instead of packed
 * in a blob. Vanilla generates the ancient debris with it.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#scattered_ore
 *
 * @property size Size of the vein, `0` to `64`, the vein places at most this many blocks.
 * @property discardChanceOnAirExposure Chance, `0` to `1`, to skip a block of the vein exposed to air.
 * @property targets The replacement targets, the first matching one wins.
 */
@Serializable
data class ScatteredOre(
	var size: Int = 0,
	var discardChanceOnAirExposure: Double = 0.0,
	override var targets: List<Target> = emptyList(),
) : FeatureConfig(), TargetsHolder

/**
 * Creates a `scattered_ore` configured feature spreading veins of up to [size] blocks, configured in [block].
 *
 * ```kotlin
 * scatteredOre("ancient_debris", size = 3, discardChanceOnAirExposure = 1.0) {
 *     targets {
 *         target(blockState(Blocks.ANCIENT_DEBRIS)) {
 *             target = tagMatch(Tags.Block.BASE_STONE_NETHER)
 *         }
 *     }
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#scattered_ore
 */
fun ConfiguredFeatures.scatteredOre(
	fileName: String,
	size: Int = 0,
	discardChanceOnAirExposure: Double = 0.0,
	block: ScatteredOre.() -> Unit = {},
) = dp.configuredFeature(fileName, ScatteredOre(size, discardChanceOnAirExposure).apply(block))
