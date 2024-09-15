package io.github.ayfri.kore.features.worldgen.templatepool.elements

import io.github.ayfri.kore.arguments.types.resources.worldgen.ProcessorListArgument
import io.github.ayfri.kore.arguments.types.resources.worldgen.StructureArgument
import io.github.ayfri.kore.features.worldgen.structures.types.jigsaw.LiquidSettings
import io.github.ayfri.kore.features.worldgen.templatepool.Projection
import io.github.ayfri.kore.features.worldgen.templatepool.TemplatePoolEntry
import kotlinx.serialization.Serializable

@Serializable
data class SinglePoolElement(
	var projection: Projection = Projection.RIGID,
	var location: StructureArgument,
	var processors: ProcessorListArgument,
	var overrideLiquidSettings: LiquidSettings? = null,
) : TemplatePoolElement()

fun MutableList<TemplatePoolEntry>.single(
	weight: Int = 0,
	projection: Projection = Projection.RIGID,
	location: StructureArgument,
	processors: ProcessorListArgument,
	overrideLiquidSettings: LiquidSettings? = null,
	block: SinglePoolElement.() -> Unit = {},
) = run {
	this += TemplatePoolEntry(weight, SinglePoolElement(projection, location, processors, overrideLiquidSettings).apply(block))
}

fun MutableList<TemplatePoolElement>.single(
	projection: Projection = Projection.RIGID,
	location: StructureArgument,
	processors: ProcessorListArgument,
	overrideLiquidSettings: LiquidSettings? = null,
	block: SinglePoolElement.() -> Unit = {},
) = run {
	this += SinglePoolElement(projection, location, processors, overrideLiquidSettings).apply(block)
}
