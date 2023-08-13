package features.worldgen.templatepool.elements

import arguments.types.resources.StructureArgument
import arguments.types.resources.worldgen.ProcessorListArgument
import features.worldgen.templatepool.Projection
import features.worldgen.templatepool.TemplatePoolEntry
import kotlinx.serialization.Serializable

@Serializable
data class SinglePoolElement(
	var projection: Projection = Projection.RIGID,
	var location: StructureArgument,
	var processors: ProcessorListArgument,
) : TemplatePoolElement()

fun MutableList<TemplatePoolEntry>.single(
	weight: Int = 0,
	projection: Projection = Projection.RIGID,
	location: StructureArgument,
	processors: ProcessorListArgument,
) = run {
	this += TemplatePoolEntry(weight, SinglePoolElement(projection, location, processors))
}

fun MutableList<TemplatePoolElement>.single(
	projection: Projection = Projection.RIGID,
	location: StructureArgument,
	processors: ProcessorListArgument,
) = run {
	this += SinglePoolElement(projection, location, processors)
}
