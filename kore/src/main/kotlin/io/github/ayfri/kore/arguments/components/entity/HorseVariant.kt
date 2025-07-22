package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.enums.HorseVariants
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = HorseVariant.Companion.HorseVariantSerializer::class)
data class HorseVariant(
	var variant: HorseVariants
) : Component() {
	companion object {
		data object HorseVariantSerializer : InlineAutoSerializer<HorseVariant>(HorseVariant::class)
	}
}

fun ComponentsScope.horseVariant(variant: HorseVariants) {
	this[EntityItemComponentTypes.HORSE_VARIANT] = HorseVariant(variant)
}
