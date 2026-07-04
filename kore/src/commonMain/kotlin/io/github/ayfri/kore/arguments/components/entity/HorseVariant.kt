package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.enums.HorseVariants
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/**
 * Represents the `minecraft:horse/variant` entity component, which sets the coat variant of a horse.
 *
 * Exposed on horse spawn eggs (and the entity itself) since snapshot 25w04a. Serializes as the variant id directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#horse/variant
 */
@Serializable(with = HorseVariant.Companion.HorseVariantSerializer::class)
data class HorseVariant(
	var variant: HorseVariants
) : Component() {
	companion object {
		data object HorseVariantSerializer : InlineAutoSerializer<HorseVariant, HorseVariants>(
			serializer<HorseVariants>(),
			HorseVariant::variant,
			::HorseVariant
		)
	}
}

/** Sets the coat variant of a horse. */
fun ComponentsScope.horseVariant(variant: HorseVariants) {
	this[EntityItemComponentTypes.HORSE_VARIANT] = HorseVariant(variant)
}
