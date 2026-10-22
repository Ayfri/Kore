package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import io.github.ayfri.kore.features.worldgen.placedfeature.PlacedFeatureOrInline
import io.github.ayfri.kore.features.worldgen.placedfeature.PlacedFeaturesScope
import io.github.ayfri.kore.features.worldgen.placedfeature.buildPlacedFeatures
import io.github.ayfri.kore.generated.arguments.worldgen.types.PlacedFeatureArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Configuration for the `simple_random_selector` feature.
 *
 * Runs one of the [features], each with the same chance. Vanilla uses it to pick between the dripleaves, the forest
 * flowers, the pointed dripstones and the warm ocean vegetation.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#simple_random_selector
 *
 * @property features A placed feature or inline placed feature, or a list of them, to pick from, it can't be empty.
 */
@Serializable
data class SimpleRandomSelector(
	var features: InlinableList<PlacedFeatureOrInline> = emptyList(),
) : FeatureConfig()

/** Creates a `simple_random_selector` configured feature picking one of the placed [features]. */
fun ConfiguredFeatures.simpleRandomSelector(
	fileName: String,
	features: InlinableList<PlacedFeatureArgument> = emptyList(),
) = dp.configuredFeature(fileName, SimpleRandomSelector(features.map { PlacedFeatureOrInline(feature = it) }))

/**
 * Creates a `simple_random_selector` configured feature picking one of the placed [features].
 *
 * ```kotlin
 * simpleRandomSelector("oak_or_birch", PlacedFeatures.OAK_CHECKED, PlacedFeatures.BIRCH_CHECKED)
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#simple_random_selector
 */
fun ConfiguredFeatures.simpleRandomSelector(fileName: String, vararg features: PlacedFeatureArgument) =
	simpleRandomSelector(fileName, features.toList())

/**
 * Creates a `simple_random_selector` configured feature picking one of the placed features declared in [block], by ID
 * with `feature(...)` or inline with `placed(...)`.
 *
 * ```kotlin
 * simpleRandomSelector("oak_or_birch") {
 *     feature(PlacedFeatures.OAK_CHECKED)
 *     placed(Features.BIRCH) { offset(y = constant(1)) }
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#simple_random_selector
 */
fun ConfiguredFeatures.simpleRandomSelector(fileName: String, block: PlacedFeaturesScope.() -> Unit) =
	dp.configuredFeature(fileName, SimpleRandomSelector(buildPlacedFeatures(block)))
