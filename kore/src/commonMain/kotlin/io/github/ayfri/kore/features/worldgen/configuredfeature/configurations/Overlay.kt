package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import io.github.ayfri.kore.features.worldgen.placedfeature.PlacedFeatureOrInline
import io.github.ayfri.kore.features.worldgen.placedfeature.PlacedFeaturesScope
import io.github.ayfri.kore.features.worldgen.placedfeature.buildPlacedFeatures
import io.github.ayfri.kore.generated.arguments.worldgen.PlacedFeatureOrTagArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Configuration for the `overlay` feature.
 *
 * Places every feature of [features] in order, whether or not the others were placed, unlike `sequence` which stops
 * at the first failure. Vanilla grows a basalt pillar with it, then the four pillars next to it, the cap of the first pillar
 * being an overlay too.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#overlay
 *
 * @property features The placed features, placed feature tag or inline placed features to place, a single one is serialized
 * bare, as a string or an object.
 */
@Serializable
data class Overlay(
	var features: InlinableList<PlacedFeatureOrInline>,
) : FeatureConfig()

/**
 * Creates an `overlay` configured feature placing every one of [features], regardless of the others' success.
 *
 * ```kotlin
 * overlay("basalt_pillars", PlacedFeatures.BASALT_BLOBS, PlacedFeatures.BASALT_PILLAR)
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#overlay
 */
fun ConfiguredFeatures.overlay(fileName: String, vararg features: PlacedFeatureOrTagArgument) =
	overlay(fileName, features.toList())

/**
 * Creates an `overlay` configured feature placing every one of [features], regardless of the others' success.
 *
 * ```kotlin
 * overlay("basalt_pillars", listOf(PlacedFeatures.BASALT_BLOBS, PlacedFeatures.BASALT_PILLAR))
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#overlay
 */
fun ConfiguredFeatures.overlay(fileName: String, features: List<PlacedFeatureOrTagArgument>) =
	dp.configuredFeature(fileName, Overlay(features.map { PlacedFeatureOrInline(feature = it) }))

/**
 * Creates an `overlay` configured feature placing every placed feature declared in [block], by ID with `feature(...)` or
 * inline with `placed(...)`, regardless of the others' success.
 *
 * ```kotlin
 * overlay("basalt_pillars") {
 *     feature(PlacedFeatures.BASALT_BLOBS)
 *     placed(Features.BASALT_PILLAR) { offset(y = constant(-2)) }
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#overlay
 */
fun ConfiguredFeatures.overlay(fileName: String, block: PlacedFeaturesScope.() -> Unit) =
	dp.configuredFeature(fileName, Overlay(buildPlacedFeatures(block)))
