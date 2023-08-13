package features.worldgen.templatepool.elements

import serializers.NamespacedPolymorphicSerializer
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
