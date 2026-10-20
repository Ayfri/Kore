package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import kotlinx.serialization.Serializable

/**
 * Configuration for the `end_platform` feature, which has no field.
 *
 * Places the obsidian platform players arrive on when entering the End.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#end_platform
 */
@Serializable
data object EndPlatform : FeatureConfig()

/** Creates an `end_platform` configured feature. */
fun ConfiguredFeatures.endPlatform(fileName: String) = dp.configuredFeature(fileName, EndPlatform)
