package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import kotlinx.serialization.Serializable

/**
 * Configuration for the `twisting_vines` feature.
 *
 * Grows twisting vines upwards from the ground around the placement position. Vanilla grows the twisting vines of the
 * warped forests with it.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#twisting_vines
 *
 * @property spreadWidth Horizontal size of the area the vines are scattered in, `1` or more.
 * @property spreadHeight Vertical size of the area the vines are scattered in, `1` or more.
 * @property maxHeight Maximum height of a vine, `1` or more.
 */
@Serializable
data class TwistingVines(
	var spreadWidth: Int = 0,
	var spreadHeight: Int = 0,
	var maxHeight: Int = 0,
) : FeatureConfig()

/**
 * Creates a `twisting_vines` configured feature scattering vines of up to [maxHeight] blocks over an area of
 * [spreadWidth] by [spreadHeight], configured in [block].
 *
 * ```kotlin
 * twistingVines("warped_vines", spreadWidth = 8, spreadHeight = 4, maxHeight = 8)
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#twisting_vines
 */
fun ConfiguredFeatures.twistingVines(
	fileName: String,
	spreadWidth: Int = 0,
	spreadHeight: Int = 0,
	maxHeight: Int = 0,
	block: TwistingVines.() -> Unit = {},
) = dp.configuredFeature(fileName, TwistingVines(spreadWidth, spreadHeight, maxHeight).apply(block))
