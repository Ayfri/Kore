package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.CatVariantArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:cat/variant` entity component, which sets the texture variant of a cat.
 *
 * Exposed on cat spawn eggs (and the entity itself) since snapshot 25w04a. Serializes as the variant id directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#cat/variant
 */
@Serializable(with = CatVariant.Companion.CatVariantSerializer::class)
data class CatVariant(
	var variant: CatVariantArgument
) : Component() {
	companion object {
		data object CatVariantSerializer : InlineAutoSerializer<CatVariant>(CatVariant::class)
	}
}

/** Sets the texture variant of a cat. */
fun ComponentsScope.catVariant(variant: CatVariantArgument) {
	this[EntityItemComponentTypes.CAT_VARIANT] = CatVariant(variant)
}
