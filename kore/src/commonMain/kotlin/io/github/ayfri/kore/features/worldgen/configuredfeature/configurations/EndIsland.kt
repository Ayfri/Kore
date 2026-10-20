package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import kotlinx.serialization.Serializable

/**
 * Configuration for the `end_island` feature, which has no field.
 *
 * Places a small floating island of end stone. Vanilla uses it for the small end islands.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#end_island
 */
@Serializable
data object EndIsland : FeatureConfig()

/** Creates an `end_island` configured feature. */
fun ConfiguredFeatures.endIsland(fileName: String) = dp.configuredFeature(fileName, EndIsland)
