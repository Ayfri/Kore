package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.enums.RabbitVariants
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/**
 * Represents the `minecraft:rabbit/variant` entity component, which sets the variant of a rabbit.
 *
 * Exposed on rabbit spawn eggs (and the entity itself) since snapshot 25w04a. Serializes as the variant id directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#rabbit/variant
 */
@Serializable(with = RabbitVariant.Companion.RabbitVariantSerializer::class)
data class RabbitVariant(
	var variant: RabbitVariants
) : Component() {
	companion object {
		data object RabbitVariantSerializer : InlineAutoSerializer<RabbitVariant, RabbitVariants>(
			serializer<RabbitVariants>(),
			RabbitVariant::variant,
			::RabbitVariant
		)
	}
}

/** Sets the variant of a rabbit. */
fun ComponentsScope.rabbitVariant(variant: RabbitVariants) {
	this[EntityItemComponentTypes.RABBIT_VARIANT] = RabbitVariant(variant)
}
