package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.CatVariantArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = CatVariant.Companion.CatVariantSerializer::class)
data class CatVariant(
	var variant: CatVariantArgument
) : Component() {
	companion object {
		data object CatVariantSerializer : InlineAutoSerializer<CatVariant>(CatVariant::class)
	}
}

fun ComponentsScope.catVariant(variant: CatVariantArgument) {
	this[EntityItemComponentTypes.CAT_VARIANT] = CatVariant(variant)
}
