package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import io.github.ayfri.kore.generated.arguments.worldgen.types.PlacedFeatureArgument
import kotlinx.serialization.Serializable

/**
 * Configuration for the `coral_claw` feature.
 *
 * Places a claw-shaped coral, [feature] being the placed feature it places on it. Vanilla picks it with the coral tree
 * in the warm ocean vegetation, and inlines a placed feature that picks a random coral colour and decorates it.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#coral_claw
 *
 * @property feature The placed feature placed on the coral, required.
 */
@Serializable
data class CoralClaw(
	var feature: PlacedFeatureArgument,
) : FeatureConfig()

/**
 * Creates a `coral_claw` configured feature placing [feature] on the coral.
 *
 * ```kotlin
 * coralClaw("warm_coral_claw", PlacedFeatureArgument("coral_overlay", "my_pack"))
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#coral_claw
 */
fun ConfiguredFeatures.coralClaw(fileName: String, feature: PlacedFeatureArgument) =
	dp.configuredFeature(fileName, CoralClaw(feature))
