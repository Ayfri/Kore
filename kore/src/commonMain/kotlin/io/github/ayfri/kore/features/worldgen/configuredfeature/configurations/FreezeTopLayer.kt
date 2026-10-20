package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import kotlinx.serialization.Serializable

/**
 * Configuration for the `freeze_top_layer` feature, which has no field.
 *
 * Freezes the surface: water turns into ice and the ground of the cold biomes gets a layer of snow. Vanilla runs it in
 * the overworld biomes.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#freeze_top_layer
 */
@Serializable
data object FreezeTopLayer : FeatureConfig()

/** Creates a `freeze_top_layer` configured feature. */
fun ConfiguredFeatures.freezeTopLayer(fileName: String) = dp.configuredFeature(fileName, FreezeTopLayer)
