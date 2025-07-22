package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.enums.RabbitVariants
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = RabbitVariant.Companion.RabbitVariantSerializer::class)
data class RabbitVariant(
	var variant: RabbitVariants
) : Component() {
	companion object {
		data object RabbitVariantSerializer : InlineAutoSerializer<RabbitVariant>(RabbitVariant::class)
	}
}

fun ComponentsScope.rabbitVariant(variant: RabbitVariants) {
	this[EntityItemComponentTypes.RABBIT_VARIANT] = RabbitVariant(variant)
}
