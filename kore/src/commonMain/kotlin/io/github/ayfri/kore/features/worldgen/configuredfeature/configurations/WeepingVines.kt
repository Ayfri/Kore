package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import kotlinx.serialization.Serializable

/**
 * Configuration for the `weeping_vines` feature, which has no field.
 *
 * Grows weeping vines hanging down from the ceiling above the placement position. Vanilla grows the weeping vines of
 * the crimson forests with it.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#weeping_vines
 */
@Serializable
data object WeepingVines : FeatureConfig()

/** Creates a `weeping_vines` configured feature, which has no configuration. */
fun ConfiguredFeatures.weepingVines(fileName: String) = dp.configuredFeature(fileName, WeepingVines)
