package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.features.worldgen.configuredfeature.ConfiguredFeatures
import io.github.ayfri.kore.features.worldgen.configuredfeature.configuredFeature
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
) = dp.configuredFeature(fileName, SimpleRandomSelector(features))

fun ConfiguredFeatures.simpleRandomSelector(fileName: String, vararg features: PlacedFeatureArgument) =
	dp.configuredFeature(fileName, SimpleRandomSelector(features.toList()))
