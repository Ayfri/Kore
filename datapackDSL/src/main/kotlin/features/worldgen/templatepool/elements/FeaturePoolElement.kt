package features.worldgen.templatepool.elements

import arguments.types.resources.worldgen.PlacedFeatureArgument
import features.worldgen.templatepool.Projection
import features.worldgen.templatepool.TemplatePoolEntry
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
