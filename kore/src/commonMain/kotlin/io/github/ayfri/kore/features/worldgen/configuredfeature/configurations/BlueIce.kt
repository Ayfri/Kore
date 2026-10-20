package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import kotlinx.serialization.Serializable

/**
 * Configuration for the `blue_ice` feature, which has no field.
 *
 * Places a blob of blue ice. Vanilla scatters it in the frozen oceans.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#blue_ice
 */
@Serializable
data object BlueIce : FeatureConfig()

/** Creates a `blue_ice` configured feature. */
fun ConfiguredFeatures.blueIce(fileName: String) = dp.configuredFeature(fileName, BlueIce)
