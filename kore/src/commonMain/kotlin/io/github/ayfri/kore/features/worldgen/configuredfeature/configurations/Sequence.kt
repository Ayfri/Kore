package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.generated.arguments.worldgen.PlacedFeatureOrTagArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:sequence` configured feature, which places multiple placed features in order.
 */
@Serializable
data class Sequence(
	var features: InlinableList<PlacedFeatureOrTagArgument>,
) : FeatureConfig()

fun ConfiguredFeatures.sequence(fileName: String, vararg features: PlacedFeatureOrTagArgument): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, Sequence(features.toList()))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}

fun ConfiguredFeatures.sequence(fileName: String, features: List<PlacedFeatureOrTagArgument>): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, Sequence(features))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}
