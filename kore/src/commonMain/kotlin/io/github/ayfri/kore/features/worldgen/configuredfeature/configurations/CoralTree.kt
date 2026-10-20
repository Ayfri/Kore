package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import kotlinx.serialization.Serializable

/**
 * Configuration for the `coral_tree` feature, which has no field.
 *
 * Places a branching coral tree, in a random colour. Vanilla picks it with the coral claw and the coral mushroom in
 * the warm ocean vegetation.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#coral_tree
 */
@Serializable
data object CoralTree : FeatureConfig()

/** Creates a `coral_tree` configured feature. */
fun ConfiguredFeatures.coralTree(fileName: String) = dp.configuredFeature(fileName, CoralTree)
