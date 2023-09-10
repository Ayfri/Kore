package io.github.ayfri.kore.features.worldgen.templatepool.elements

import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@Serializable(with = TemplatePoolElement.Companion.TemplatePoolElementSerializer::class)
sealed class TemplatePoolElement {
	companion object {
		data object TemplatePoolElementSerializer : NamespacedPolymorphicSerializer<TemplatePoolElement>(
			TemplatePoolElement::class,
			outputName = "element_type"
		)
	}
}
