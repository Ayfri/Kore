package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import kotlinx.serialization.Serializable

/**
 * Configuration for the `sculk_patch` feature.
 *
 * Spreads charges of sculk around the placement position. Vanilla grows the sculk patches of the deep dark and of the
 * ancient cities with it, then places a sculk catalyst, plus shriekers for ancient cities, with `simple_block` features
 * in a `sequence`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#sculk_patch
 *
 * @property chargeCount Number of charges, `1` to `32`.
 * @property amountPerCharge Initial value of each charge, `1` to `500`.
 * @property spreadAttempts Number of attempts to spread, `1` to `64`.
 * @property growthRounds Number of times the patch grows, `0` to `8`.
 * @property spreadRounds Number of times the patch spreads, `0` to `8`.
 */
@Serializable
data class SculkPatch(
	var chargeCount: Int = 1,
	var amountPerCharge: Int = 1,
	var spreadAttempts: Int = 1,
	var growthRounds: Int = 0,
	var spreadRounds: Int = 0,
) : FeatureConfig()

/**
 * Creates a `sculk_patch` configured feature, configured in [block].
 *
 * ```kotlin
 * sculkPatch("deep_dark_patch") {
 *     chargeCount = 10
 *     amountPerCharge = 32
 *     spreadAttempts = 64
 *     growthRounds = 0
 *     spreadRounds = 1
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#sculk_patch
 */
fun ConfiguredFeatures.sculkPatch(fileName: String, block: SculkPatch.() -> Unit = {}) =
	dp.configuredFeature(fileName, SculkPatch().apply(block))
