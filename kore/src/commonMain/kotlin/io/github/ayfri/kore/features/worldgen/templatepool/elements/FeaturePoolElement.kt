package io.github.ayfri.kore.features.worldgen.templatepool.elements

import io.github.ayfri.kore.features.worldgen.templatepool.Projection
import io.github.ayfri.kore.features.worldgen.templatepool.TemplatePoolEntry
import io.github.ayfri.kore.generated.arguments.worldgen.types.PlacedFeatureArgument
import kotlinx.serialization.Serializable

@Serializable
data class FeaturePoolElement(
	var projection: Projection = Projection.RIGID,
	var feature: PlacedFeatureArgument,
) : TemplatePoolElement()

fun MutableList<TemplatePoolEntry>.feature(
	weight: Int = 0,
	projection: Projection = Projection.RIGID,
	feature: PlacedFeatureArgument,
) = run {
	this += TemplatePoolEntry(weight, FeaturePoolElement(projection, feature))
}

fun MutableList<TemplatePoolElement>.feature(
	projection: Projection = Projection.RIGID,
	feature: PlacedFeatureArgument,
) = run {
	this += FeaturePoolElement(projection, feature)
}
