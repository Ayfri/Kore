package features.worldgen.templatepool.elements

import features.worldgen.templatepool.Projection
import features.worldgen.templatepool.TemplatePoolEntry
import kotlinx.serialization.Serializable

@Serializable
data class ListPoolElement(
	var projection: Projection = Projection.RIGID,
	var elements: List<TemplatePoolElement> = emptyList(),
) : TemplatePoolElement()

fun MutableList<TemplatePoolEntry>.list(
	weight: Int = 0,
	projection: Projection = Projection.RIGID,
	elements: MutableList<TemplatePoolElement>.() -> Unit = {},
) = run {
	this += TemplatePoolEntry(weight, ListPoolElement(projection, buildList(elements)))
}
