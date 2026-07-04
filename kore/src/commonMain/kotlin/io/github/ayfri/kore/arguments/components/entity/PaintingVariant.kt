package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.PaintingVariantArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/**
 * Represents the `minecraft:painting/variant` entity component, which sets the motif of a painting.
 *
 * Exposed on painting items (and the entity itself) since snapshot 25w04a. Serializes as the variant id directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#painting/variant
 */
@Serializable(with = PaintingVariant.Companion.PaintingVariantSerializer::class)
data class PaintingVariant(
	var variant: PaintingVariantArgument
) : Component() {
	companion object {
		data object PaintingVariantSerializer : InlineAutoSerializer<PaintingVariant, PaintingVariantArgument>(
			serializer<PaintingVariantArgument>(),
			PaintingVariant::variant,
			::PaintingVariant
		)
	}
}

/** Sets the motif of a painting. */
fun ComponentsScope.paintingVariant(variant: PaintingVariantArgument) {
	this[EntityItemComponentTypes.PAINTING_VARIANT] = PaintingVariant(variant)
}
