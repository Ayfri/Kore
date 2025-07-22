package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.enums.MooshroomVariants
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = MooshroomVariant.Companion.MooshroomVariantSerializer::class)
data class MooshroomVariant(
	var variant: MooshroomVariants
) : Component() {
	companion object {
		data object MooshroomVariantSerializer : InlineAutoSerializer<MooshroomVariant>(MooshroomVariant::class)
	}
}

fun ComponentsScope.mooshroomVariant(variant: MooshroomVariants) {
	this[EntityItemComponentTypes.MOOSHROOM_VARIANT] = MooshroomVariant(variant)
}
