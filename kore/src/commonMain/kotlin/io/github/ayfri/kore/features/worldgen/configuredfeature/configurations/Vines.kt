package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import kotlinx.serialization.Serializable

/**
 * Configuration for the `vines` feature, which has no field.
 *
 * Places a vines block at the placement position, attached to an adjacent block. Vanilla places it in the jungles and
 * the lush caves.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#vines
 */
@Serializable
data object Vines : FeatureConfig()

/** Creates a `vines` configured feature, which has no configuration. */
fun ConfiguredFeatures.vines(fileName: String) = dp.configuredFeature(fileName, Vines)
