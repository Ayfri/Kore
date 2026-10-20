package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
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
 * Configuration for the `weighted_random_selector` feature.
 *
 * Runs one of the [features], picked with a probability proportional to its weight. Vanilla uses it to choose between
 * the sizes of its basalt columns and of its sulfur springs.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
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
 * Creates a `weighted_random_selector` configured feature running one of its entries by weight, configured in
 * [block].
 *
 * ```kotlin
 * weightedRandomSelector("mixed_trees") {
 *     feature(PlacedFeatures.OAK_CHECKED, weight = 3)
 *     feature(PlacedFeatures.BIRCH_CHECKED, weight = 1)
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#weighted_random_selector
 */
fun ConfiguredFeatures.weightedRandomSelector(
	fileName: String,
	block: WeightedRandomSelector.() -> Unit = {},
) = dp.configuredFeature(fileName, WeightedRandomSelector().apply(block))
