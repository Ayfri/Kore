package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.enums.LlamaVariants
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:llama/variant` entity component, which sets the variant of a llama.
 *
 * Exposed on llama spawn eggs (and the entity itself) since snapshot 25w04a. Serializes as the variant id directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#llama/variant
 */
@Serializable(with = LlamaVariant.Companion.LlamaVariantSerializer::class)
data class LlamaVariant(
	var variant: LlamaVariants
) : Component() {
	companion object {
		data object LlamaVariantSerializer : InlineAutoSerializer<LlamaVariant>(LlamaVariant::class)
	}
}

/** Sets the variant of a llama. */
fun ComponentsScope.llamaVariant(variant: LlamaVariants) {
	this[EntityItemComponentTypes.LLAMA_VARIANT] = LlamaVariant(variant)
}
