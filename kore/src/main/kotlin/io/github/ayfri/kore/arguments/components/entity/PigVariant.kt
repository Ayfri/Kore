package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.PigVariantArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = PigVariant.Companion.PigVariantSerializer::class)
data class PigVariant(
	var variant: PigVariantArgument
) : Component() {
	companion object {
		data object PigVariantSerializer : InlineAutoSerializer<PigVariant>(PigVariant::class)
	}
}

fun ComponentsScope.pigVariant(variant: PigVariantArgument) {
	this[EntityItemComponentTypes.PIG_VARIANT] = PigVariant(variant)
}
