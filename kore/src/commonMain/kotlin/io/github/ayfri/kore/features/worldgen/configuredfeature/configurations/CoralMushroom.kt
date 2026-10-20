package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import kotlinx.serialization.Serializable

/**
 * Configuration for the `coral_mushroom` feature, which has no field.
 *
 * Places a mushroom-shaped coral, in a random colour. Vanilla picks it with the coral tree and the coral claw in the
 * warm ocean vegetation.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#coral_mushroom
 */
@Serializable
data object CoralMushroom : FeatureConfig()

/** Creates a `coral_mushroom` configured feature. */
fun ConfiguredFeatures.coralMushroom(fileName: String) = dp.configuredFeature(fileName, CoralMushroom)
