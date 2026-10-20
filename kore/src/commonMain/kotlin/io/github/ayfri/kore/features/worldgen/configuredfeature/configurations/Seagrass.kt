package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import kotlinx.serialization.Serializable

/**
 * Configuration for the `seagrass` feature.
 *
 * Places seagrass on the sea floor, either a tall two-block one or a short one. Vanilla places the seagrass of its
 * oceans, rivers and swamps with it.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#seagrass
 *
 * @property probability Chance, `0` to `1`, of placing tall seagrass instead of short seagrass.
 */
@Serializable
data class Seagrass(
	var probability: Double = 0.0,
) : FeatureConfig()

/**
 * Creates a `seagrass` configured feature placing tall seagrass with the given [probability].
 *
 * ```kotlin
 * seagrass("tall_seagrass", probability = 0.8)
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#seagrass
 */
fun ConfiguredFeatures.seagrass(fileName: String, probability: Double = 0.0) =
	dp.configuredFeature(fileName, Seagrass(probability))
