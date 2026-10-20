package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import io.github.ayfri.kore.generated.arguments.worldgen.types.PlacedFeatureArgument
import kotlinx.serialization.Serializable

/**
 * Configuration for the `coral_tree` feature.
 *
 * Places a branching coral tree, [feature] being the placed feature it places on it. Vanilla picks it with the coral
 * claw in the warm ocean vegetation, and inlines a placed feature that picks a random coral colour and decorates it.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#coral_tree
 *
 * @property feature The placed feature placed on the coral, required.
 */
@Serializable
data class CoralTree(
	var feature: PlacedFeatureArgument,
) : FeatureConfig()

/**
 * Creates a `coral_tree` configured feature placing [feature] on the coral.
 *
 * ```kotlin
 * coralTree("warm_coral_tree", PlacedFeatureArgument("coral_overlay", "my_pack"))
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#coral_tree
 */
fun ConfiguredFeatures.coralTree(fileName: String, feature: PlacedFeatureArgument) =
	dp.configuredFeature(fileName, CoralTree(feature))
