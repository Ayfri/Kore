package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import kotlinx.serialization.Serializable

/**
 * Configuration for the `end_podium` feature.
 *
 * Places the podium of the End exit portal. Vanilla ships an active and an inactive variant.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#end_podium
 *
 * @property active Whether the exit portal is lit, `false` when `null`.
 */
@Serializable
data class EndPodium(
	var active: Boolean? = null,
) : FeatureConfig()

/**
 * Creates an `end_podium` configured feature, with a lit exit portal when [active].
 *
 * ```kotlin
 * endPodium("end_podium_active", active = true)
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#end_podium
 */
fun ConfiguredFeatures.endPodium(fileName: String, active: Boolean? = null) =
	dp.configuredFeature(fileName, EndPodium(active))
