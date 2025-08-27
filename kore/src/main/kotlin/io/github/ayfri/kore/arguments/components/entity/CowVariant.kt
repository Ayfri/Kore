package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.CowVariantArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = CowVariant.Companion.CowVariantSerializer::class)
data class CowVariant(
	var variant: CowVariantArgument
) : Component() {
	companion object {
		data object CowVariantSerializer : InlineAutoSerializer<CowVariant>(CowVariant::class)
	}
}

fun ComponentsScope.cowVariant(variant: CowVariantArgument) {
	this[EntityItemComponentTypes.COW_VARIANT] = CowVariant(variant)
}
