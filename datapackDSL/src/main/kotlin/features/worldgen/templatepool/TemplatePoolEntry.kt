package features.worldgen.templatepool

import features.worldgen.templatepool.elements.TemplatePoolElement
import kotlinx.serialization.Serializable

@Serializable
data class TemplatePoolEntry(
	var weight: Int = 0,
	var element: TemplatePoolElement,
)
