package io.github.ayfri.kore.features.worldgen.templatepool.elements

import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import io.github.ayfri.kore.serializers.NamespacedPolymorphicSerializer
import kotlinx.serialization.Serializable

@GeneratedSealedSerializer
@Serializable(with = TemplatePoolElement.Companion.TemplatePoolElementSerializer::class)
sealed class TemplatePoolElement {
	companion object {
		data object TemplatePoolElementSerializer : NamespacedPolymorphicSerializer<TemplatePoolElement>(
			templatePoolElementSealedSerializer(),
			outputName = "element_type"
		)
	}
}
