package io.github.ayfri.kore.features.worldgen.templatepool.elements

import io.github.ayfri.kore.features.worldgen.templatepool.Projection
import io.github.ayfri.kore.features.worldgen.templatepool.TemplatePoolEntry
import io.github.ayfri.kore.generated.arguments.worldgen.types.ProcessorListArgument
import io.github.ayfri.kore.generated.arguments.worldgen.types.StructureArgument
import kotlinx.serialization.Serializable

@Serializable
data class LegacySinglePoolElement(
	var projection: Projection = Projection.RIGID,
	var location: StructureArgument,
	var processors: ProcessorListArgument,
) : TemplatePoolElement()

fun MutableList<TemplatePoolEntry>.legacySingle(
	weight: Int = 0,
	projection: Projection = Projection.RIGID,
	location: StructureArgument,
	processors: ProcessorListArgument,
) = run {
	this += TemplatePoolEntry(weight, LegacySinglePoolElement(projection, location, processors))
}

fun MutableList<TemplatePoolElement>.legacySingle(
	projection: Projection = Projection.RIGID,
	location: StructureArgument,
	processors: ProcessorListArgument,
) = run {
	this += LegacySinglePoolElement(projection, location, processors)
}
