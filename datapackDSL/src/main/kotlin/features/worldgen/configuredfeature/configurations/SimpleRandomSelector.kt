package features.worldgen.configuredfeature.configurations

import arguments.types.resources.worldgen.PlacedFeatureArgument
import serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class SimpleRandomSelector(
	var features: InlinableList<PlacedFeatureArgument> = emptyList(),
) : FeatureConfig()

fun simpleRandomSelector(features: InlinableList<PlacedFeatureArgument> = emptyList()) = SimpleRandomSelector(features)

fun simpleRandomSelector(vararg features: PlacedFeatureArgument) = SimpleRandomSelector(features.toList())
