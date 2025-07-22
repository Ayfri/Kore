package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.enums.LlamaVariants
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = LlamaVariant.Companion.LlamaVariantSerializer::class)
data class LlamaVariant(
	var variant: LlamaVariants
) : Component() {
	companion object {
		data object LlamaVariantSerializer : InlineAutoSerializer<LlamaVariant>(LlamaVariant::class)
	}
}

fun ComponentsScope.llamaVariant(variant: LlamaVariants) {
	this[EntityItemComponentTypes.LLAMA_VARIANT] = LlamaVariant(variant)
}
