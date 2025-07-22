package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.Components
import io.github.ayfri.kore.generated.arguments.types.PaintingVariantArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.arguments.components.ComponentsScope

@Serializable(with = PaintingVariant.Companion.PaintingVariantSerializer::class)
data class PaintingVariant(
	var variant: PaintingVariantArgument
) : Component() {
	companion object {
		data object PaintingVariantSerializer : InlineAutoSerializer<PaintingVariant>(PaintingVariant::class)
	}
}

fun ComponentsScope.paintingVariant(variant: PaintingVariantArgument) {
	this[EntityItemComponentTypes.PAINTING_VARIANT] = PaintingVariant(variant)
}
