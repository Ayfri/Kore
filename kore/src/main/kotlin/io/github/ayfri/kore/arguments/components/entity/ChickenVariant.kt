package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.ChickenVariantArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:chicken/variant` entity component, which sets the variant of a chicken.
 *
 * Exposed on chicken spawn eggs (and the entity itself) since snapshot 25w04a. Serializes as the variant id directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#chicken/variant
 */
@Serializable(with = ChickenVariant.Companion.ChickenVariantSerializer::class)
data class ChickenVariant(
	var variant: ChickenVariantArgument
) : Component() {
	companion object {
		data object ChickenVariantSerializer : InlineAutoSerializer<ChickenVariant>(ChickenVariant::class)
	}
}

/** Sets the variant of a chicken. */
fun ComponentsScope.chickenVariant(variant: ChickenVariantArgument) {
	this[EntityItemComponentTypes.CHICKEN_VARIANT] = ChickenVariant(variant)
}
