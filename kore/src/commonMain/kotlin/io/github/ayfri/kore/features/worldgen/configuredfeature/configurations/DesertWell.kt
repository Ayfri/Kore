package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import kotlinx.serialization.Serializable

/**
 * Configuration for the `desert_well` feature, which has no field.
 *
 * Places a desert well, a small sandstone well with water. Vanilla uses it in the deserts.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#desert_well
 */
@Serializable
data object DesertWell : FeatureConfig()

/** Creates a `desert_well` configured feature. */
fun ConfiguredFeatures.desertWell(fileName: String) = dp.configuredFeature(fileName, DesertWell)
