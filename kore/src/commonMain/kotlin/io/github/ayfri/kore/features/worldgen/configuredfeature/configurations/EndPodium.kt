package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.generated.arguments.worldgen.types.FeatureArgument
import kotlinx.serialization.Serializable

/**
 * Places the End exit portal podium.
 *
 * @property active Whether the exit portal is lit, defaults to `false`.
 */
@Serializable
data class EndPodium(
	var active: Boolean? = null,
) : FeatureConfig()

/**
 * Creates an `end_podium` configured feature, with a lit exit portal when [active].
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 */
fun ConfiguredFeatures.endPodium(fileName: String, active: Boolean? = null): FeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, EndPodium(active))
	dp.configuredFeatures += configuredFeature
	return FeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}
