package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.enums.FoxVariants
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = FoxVariant.Companion.FoxVariantSerializer::class)
data class FoxVariant(
	var variant: FoxVariants
) : Component() {
	companion object {
		data object FoxVariantSerializer : InlineAutoSerializer<FoxVariant>(FoxVariant::class)
	}
}

fun ComponentsScope.foxVariant(variant: FoxVariants) {
	this[EntityItemComponentTypes.FOX_VARIANT] = FoxVariant(variant)
}
