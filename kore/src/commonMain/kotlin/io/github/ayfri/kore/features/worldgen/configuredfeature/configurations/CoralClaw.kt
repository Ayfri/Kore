package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import kotlinx.serialization.Serializable

/**
 * Configuration for the `coral_claw` feature, which has no field.
 *
 * Places a claw-shaped coral, in a random colour. Vanilla picks it with the coral tree and the coral mushroom in the
 * warm ocean vegetation.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#coral_claw
 */
@Serializable
data object CoralClaw : FeatureConfig()

/** Creates a `coral_claw` configured feature. */
fun ConfiguredFeatures.coralClaw(fileName: String) = dp.configuredFeature(fileName, CoralClaw)
