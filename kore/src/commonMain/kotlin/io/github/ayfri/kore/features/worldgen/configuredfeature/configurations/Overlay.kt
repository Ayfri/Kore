package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import io.github.ayfri.kore.generated.arguments.worldgen.PlacedFeatureOrTagArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Configuration for the `overlay` feature.
 *
 * Places every feature of [features] in order, whether or not the others were placed, unlike `sequence` which stops
 * at the first failure. Vanilla grows a basalt pillar and the four pillars around it with it, and builds their cap.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 * Minecraft Wiki: https://minecraft.wiki/w/Configured_feature#overlay
 *
 * @property features The placed features or placed feature tag to place, a single one is serialized as a bare string.
 */
@Serializable
data class Overlay(
	var features: InlinableList<PlacedFeatureOrTagArgument>,
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
	dp.configuredFeature(fileName, Overlay(features))
