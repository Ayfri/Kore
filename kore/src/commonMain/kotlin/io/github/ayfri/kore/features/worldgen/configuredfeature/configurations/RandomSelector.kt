package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import io.github.ayfri.kore.generated.arguments.worldgen.types.PlacedFeatureArgument
import kotlinx.serialization.Serializable

/**
 * Configuration for the `random_selector` feature.
 *
 * Rolls the [features] in order, each with its own chance, and runs the first one that succeeds, or [default] when none
 * does. Vanilla uses it to mix the tree kinds of its forests.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#random_selector
 *
 * @property features The entries rolled in order.
 * @property default The placed feature run when no entry succeeds.
 */
@Serializable
data class RandomSelector(
	var features: List<RandomSelectorFeature> = emptyList(),
	var default: PlacedFeatureArgument,
) : FeatureConfig()

/**
 * An entry of a [RandomSelector].
 *
 * @property chance Chance, `0` to `1`, that this entry is picked.
 * @property feature The placed feature run when the entry is picked.
 */
@Serializable
data class RandomSelectorFeature(
	var chance: Float,
	var feature: PlacedFeatureArgument,
)

/** Appends an entry running [feature] with the given [chance]. */
fun RandomSelector.feature(feature: PlacedFeatureArgument, chance: Float) {
	features += RandomSelectorFeature(chance, feature)
}

/**
 * Creates a `random_selector` configured feature running [default] when none of its entries succeeds, configured in
 * [block].
 *
 * ```kotlin
 * randomSelector("plains_trees", default = PlacedFeatures.OAK_CHECKED) {
 *     feature(PlacedFeatures.FANCY_OAK_CHECKED, chance = 0.33f)
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#random_selector
 */
fun ConfiguredFeatures.randomSelector(
	fileName: String,
	default: PlacedFeatureArgument,
	block: RandomSelector.() -> Unit = {},
) = dp.configuredFeature(fileName, RandomSelector(default = default).apply(block))
