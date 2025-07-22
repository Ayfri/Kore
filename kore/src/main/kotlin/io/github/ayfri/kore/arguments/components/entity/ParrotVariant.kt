package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.enums.ParrotVariants
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = ParrotVariant.Companion.ParrotVariantSerializer::class)
data class ParrotVariant(
	var variant: ParrotVariants
) : Component() {
	companion object {
		data object ParrotVariantSerializer : InlineAutoSerializer<ParrotVariant>(ParrotVariant::class)
	}
}

fun ComponentsScope.parrotVariant(variant: ParrotVariants) {
	this[EntityItemComponentTypes.PARROT_VARIANT] = ParrotVariant(variant)
}
