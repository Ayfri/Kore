package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import kotlinx.serialization.Serializable

/**
 * Configuration for the `chorus_plant` feature, which has no field.
 *
 * Grows a chorus plant with its flowers. Vanilla places it on the end stone of the end highlands.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#chorus_plant
 */
@Serializable
data object ChorusPlant : FeatureConfig()

/** Creates a `chorus_plant` configured feature. */
fun ConfiguredFeatures.chorusPlant(fileName: String) = dp.configuredFeature(fileName, ChorusPlant)
