package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.Target
import io.github.ayfri.kore.features.worldgen.configuredfeature.TargetsHolder
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import kotlinx.serialization.Serializable

/**
 * Configuration for the `ore` feature.
 *
 * Places a vein of blocks replacing the terrain around the placement position. Vanilla generates its ore veins with
 * it, along with the dirt, gravel and stone variant blobs and the nether magma blocks.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#ore
 *
 * @property size Size of the vein, `0` to `64`, the vein places at most this many blocks.
 * @property discardChanceOnAirExposure Chance, `0` to `1`, to skip a block of the vein exposed to air.
 * @property targets The replacement targets, the first matching one wins.
 */
@Serializable
data class Ore(
	var size: Int = 0,
	var discardChanceOnAirExposure: Double = 0.0,
	override var targets: List<Target> = emptyList(),
) : FeatureConfig(), TargetsHolder

/**
 * Creates an `ore` configured feature placing veins of up to [size] blocks, configured in [block].
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
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#ore
 */
fun ConfiguredFeatures.ore(
	fileName: String,
	size: Int = 0,
	discardChanceOnAirExposure: Double = 0.0,
	block: Ore.() -> Unit = {},
) = dp.configuredFeature(fileName, Ore(size, discardChanceOnAirExposure).apply(block))
