package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import kotlinx.serialization.Serializable

/**
 * Configuration for the `kelp` feature, which has no field.
 *
 * Grows a kelp stalk up from the sea floor. Vanilla uses it in the oceans, as `kelp_cold` and `kelp_warm`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#kelp
 */
@Serializable
data object Kelp : FeatureConfig()

/** Creates a `kelp` configured feature. */
fun ConfiguredFeatures.kelp(fileName: String) = dp.configuredFeature(fileName, Kelp)
