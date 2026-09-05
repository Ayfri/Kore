package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeature
import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.generated.arguments.worldgen.types.ConfiguredFeatureArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.PlacedFeatureArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class SimpleRandomSelector(
	var features: InlinableList<PlacedFeatureArgument> = emptyList(),
) : FeatureConfig()

fun ConfiguredFeatures.simpleRandomSelector(
	fileName: String,
	features: InlinableList<PlacedFeatureArgument> = emptyList(),
): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, SimpleRandomSelector(features))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}

fun ConfiguredFeatures.simpleRandomSelector(fileName: String, vararg features: PlacedFeatureArgument): ConfiguredFeatureArgument {
	val configuredFeature = ConfiguredFeature(fileName, SimpleRandomSelector(features.toList()))
	dp.configuredFeatures += configuredFeature
	return ConfiguredFeatureArgument(fileName, configuredFeature.namespace ?: dp.name)
}
