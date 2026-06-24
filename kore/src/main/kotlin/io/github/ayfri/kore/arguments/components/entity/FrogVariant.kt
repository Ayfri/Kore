package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.FrogVariantArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:frog/variant` entity component, which sets the variant of a frog.
 *
 * Exposed on frog spawn eggs (and the entity itself) since snapshot 25w04a. Serializes as the variant id directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#frog/variant
 */
@Serializable(with = FrogVariant.Companion.FrogVariantSerializer::class)
data class FrogVariant(
	var variant: FrogVariantArgument
) : Component() {
	companion object {
		data object FrogVariantSerializer : InlineAutoSerializer<FrogVariant>(FrogVariant::class)
	}
}

/** Sets the variant of a frog. */
fun ComponentsScope.frogVariant(variant: FrogVariantArgument) {
	this[EntityItemComponentTypes.FROG_VARIANT] = FrogVariant(variant)
}
