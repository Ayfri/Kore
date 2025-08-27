package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.ChickenVariantArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = ChickenVariant.Companion.ChickenVariantSerializer::class)
data class ChickenVariant(
	var variant: ChickenVariantArgument
) : Component() {
	companion object {
		data object ChickenVariantSerializer : InlineAutoSerializer<ChickenVariant>(ChickenVariant::class)
	}
}

fun ComponentsScope.chickenVariant(variant: ChickenVariantArgument) {
	this[EntityItemComponentTypes.CHICKEN_VARIANT] = ChickenVariant(variant)
}
