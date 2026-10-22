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
 * Represents the `minecraft:sequence` configured feature, which places multiple placed features in order.
 */
@Serializable
data class Sequence(
	var features: InlinableList<PlacedFeatureOrInline>,
) : FeatureConfig()

/**
 * Creates a `sequence` configured feature placing [features] in order, stopping at the first one that is not placed.
 *
 * ```kotlin
 * sequence("tree_then_flowers", PlacedFeatures.ACACIA, PlacedFeatures.FLOWER_DEFAULT)
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 */
fun ConfiguredFeatures.sequence(fileName: String, vararg features: PlacedFeatureOrTagArgument) =
	sequence(fileName, features.toList())

/**
 * Creates a `sequence` configured feature placing [features] in order, stopping at the first one that is not placed.
 *
 * ```kotlin
 * sequence("tree_then_flowers", listOf(PlacedFeatures.ACACIA, PlacedFeatures.FLOWER_DEFAULT))
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 */
fun ConfiguredFeatures.sequence(fileName: String, features: List<PlacedFeatureOrTagArgument>) =
	dp.configuredFeature(fileName, Sequence(features.map { PlacedFeatureOrInline(feature = it) }))

/**
 * Creates a `sequence` configured feature placing the placed features declared in [block] in order, by ID with
 * `feature(...)` or inline with `placed(...)`, stopping at the first one that is not placed.
 *
 * ```kotlin
 * sequence("tree_then_flowers") {
 *     feature(PlacedFeatures.ACACIA)
 *     placed(Features.FLOWER_DEFAULT) { offset(y = constant(1)) }
 * }
 * ```
 *
 * Produces `data/<namespace>/worldgen/feature/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/worldgen/features
 */
fun ConfiguredFeatures.sequence(fileName: String, block: PlacedFeaturesScope.() -> Unit) =
	dp.configuredFeature(fileName, Sequence(buildPlacedFeatures(block)))
