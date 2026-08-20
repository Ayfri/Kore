package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.PlacedFeatureArgument
import kotlinx.serialization.Serializable

/**
 * A single weighted entry of a [WeightedRandomSelector].
 *
 * @property feature The placed feature this entry resolves to.
 * @property weight This entry's weight, relative to the other entries of the selector.
 */
@Serializable
data class WeightedPlacedFeature(
	var feature: PlacedFeatureArgument,
	var weight: Int,
)

/**
 * Configuration for the `weighted_random_selector` feature, picking one of [features] with a probability
 * proportional to its weight.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#weighted_random_selector
 *
 * @property features The weighted entries to pick from.
 */
@Serializable
data class WeightedRandomSelector(
	var features: List<WeightedPlacedFeature> = emptyList(),
) : FeatureConfig()

/** Appends a weighted entry picking [feature] with the given [weight]. */
fun WeightedRandomSelector.feature(feature: PlacedFeatureArgument, weight: Int) {
	features += WeightedPlacedFeature(feature, weight)
}

/**
 * Creates a `weighted_random_selector` configured feature, picking one of [WeightedRandomSelector.features] with a
 * probability proportional to its weight.
 *
 * ```kotlin
 * weightedRandomSelector("mixed_ore") {
 *     feature(PlacedFeatures.ORE_IRON, weight = 3)
 *     feature(PlacedFeatures.ORE_GOLD, weight = 1)
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/configured_feature/<fileName>.json`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#weighted_random_selector
 */
fun ConfiguredFeatures.weightedRandomSelector(
	fileName: String,
	block: WeightedRandomSelector.() -> Unit = {},
): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, WeightedRandomSelector().apply(block))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}
