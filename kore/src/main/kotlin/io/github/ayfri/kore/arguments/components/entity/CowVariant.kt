package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.CowVariantArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/**
 * Represents the `minecraft:cow/variant` entity component, which sets the variant of a cow.
 *
 * Exposed on cow spawn eggs (and the entity itself) since snapshot 25w04a. Serializes as the variant id directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#cow/variant
 */
@Serializable(with = CowVariant.Companion.CowVariantSerializer::class)
data class CowVariant(
	var variant: CowVariantArgument
) : Component() {
	companion object {
		data object CowVariantSerializer : InlineAutoSerializer<CowVariant, CowVariantArgument>(
			serializer<CowVariantArgument>(),
			CowVariant::variant,
			::CowVariant
		)
	}
}

/** Sets the variant of a cow. */
fun ComponentsScope.cowVariant(variant: CowVariantArgument) {
	this[EntityItemComponentTypes.COW_VARIANT] = CowVariant(variant)
}
