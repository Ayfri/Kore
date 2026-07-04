package io.github.ayfri.kore.features.worldgen.configuredfeature.configurations

import io.github.ayfri.kore.generated.arguments.worldgen.types.PlacedFeatureArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class SimpleRandomSelector(
	var features: InlinableList<PlacedFeatureArgument> = emptyList(),
) : FeatureConfig()

fun simpleRandomSelector(features: InlinableList<PlacedFeatureArgument> = emptyList()) = SimpleRandomSelector(features)

fun simpleRandomSelector(vararg features: PlacedFeatureArgument) = SimpleRandomSelector(features.toList())
