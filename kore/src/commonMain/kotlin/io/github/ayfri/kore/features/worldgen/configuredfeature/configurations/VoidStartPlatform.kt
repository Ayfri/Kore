package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import kotlinx.serialization.Serializable

/**
 * Configuration for the `void_start_platform` feature, which has no field.
 *
 * Builds a 33x33 platform of stone with a cobblestone block in its center, the spawn platform of the void. Vanilla
 * places it in the `the_void` biome.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#void_start_platform
 */
@Serializable
data object VoidStartPlatform : FeatureConfig()

/** Creates a `void_start_platform` configured feature, which has no configuration. */
fun ConfiguredFeatures.voidStartPlatform(fileName: String) = dp.configuredFeature(fileName, VoidStartPlatform)
