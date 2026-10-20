package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
import io.github.ayfri.kore.generated.arguments.worldgen.PlacedFeatureOrTagArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:sequence` configured feature, which places multiple placed features in order.
 */
@Serializable
data class Sequence(
	var features: InlinableList<PlacedFeatureOrTagArgument>,
) : FeatureConfig()

fun ConfiguredFeatures.sequence(fileName: String, vararg features: PlacedFeatureOrTagArgument) =
	dp.configuredFeature(fileName, Sequence(features.toList()))

fun ConfiguredFeatures.sequence(fileName: String, features: List<PlacedFeatureOrTagArgument>) =
	dp.configuredFeature(fileName, Sequence(features))
