package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.enums.FoxVariants
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:fox/variant` entity component, which sets the variant of a fox (red or snow).
 *
 * Exposed on fox spawn eggs (and the entity itself) since snapshot 25w04a. Serializes as the variant id directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#fox/variant
 */
@Serializable(with = FoxVariant.Companion.FoxVariantSerializer::class)
data class FoxVariant(
	var variant: FoxVariants
) : Component() {
	companion object {
		data object FoxVariantSerializer : InlineAutoSerializer<FoxVariant>(FoxVariant::class)
	}
}

/** Sets the variant of a fox (red or snow). */
fun ComponentsScope.foxVariant(variant: FoxVariants) {
	this[EntityItemComponentTypes.FOX_VARIANT] = FoxVariant(variant)
}
