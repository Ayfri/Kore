package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.enums.ParrotVariants
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/**
 * Represents the `minecraft:parrot/variant` entity component, which sets the color variant of a parrot.
 *
 * Exposed on parrot spawn eggs (and the entity itself) since snapshot 25w04a. Serializes as the variant id directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#parrot/variant
 */
@Serializable(with = ParrotVariant.Companion.ParrotVariantSerializer::class)
data class ParrotVariant(
	var variant: ParrotVariants
) : Component() {
	companion object {
		data object ParrotVariantSerializer : InlineAutoSerializer<ParrotVariant, ParrotVariants>(
			serializer<ParrotVariants>(),
			ParrotVariant::variant,
			::ParrotVariant
		)
	}
}

/** Sets the color variant of a parrot. */
fun ComponentsScope.parrotVariant(variant: ParrotVariants) {
	this[EntityItemComponentTypes.PARROT_VARIANT] = ParrotVariant(variant)
}
