package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.PigVariantArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:pig/variant` entity component, which sets the variant of a pig.
 *
 * Exposed on pig spawn eggs (and the entity itself) since snapshot 25w04a. Serializes as the variant id directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#pig/variant
 */
@Serializable(with = PigVariant.Companion.PigVariantSerializer::class)
data class PigVariant(
	var variant: PigVariantArgument
) : Component() {
	companion object {
		data object PigVariantSerializer : InlineAutoSerializer<PigVariant>(PigVariant::class)
	}
}

/** Sets the variant of a pig. */
fun ComponentsScope.pigVariant(variant: PigVariantArgument) {
	this[EntityItemComponentTypes.PIG_VARIANT] = PigVariant(variant)
}
