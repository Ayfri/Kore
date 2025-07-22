package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.Components
import io.github.ayfri.kore.generated.arguments.types.FrogVariantArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.arguments.components.ComponentsScope

@Serializable(with = FrogVariant.Companion.FrogVariantSerializer::class)
data class FrogVariant(
	var variant: FrogVariantArgument
) : Component() {
	companion object {
		data object FrogVariantSerializer : InlineAutoSerializer<FrogVariant>(FrogVariant::class)
	}
}

fun ComponentsScope.frogVariant(variant: FrogVariantArgument) {
	this[EntityItemComponentTypes.FROG_VARIANT] = FrogVariant(variant)
}
