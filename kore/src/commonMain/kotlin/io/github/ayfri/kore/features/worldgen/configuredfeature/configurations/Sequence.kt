package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

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

fun sequence(vararg features: PlacedFeatureOrTagArgument) = Sequence(features.toList())

fun sequence(features: List<PlacedFeatureOrTagArgument>) = Sequence(features)
