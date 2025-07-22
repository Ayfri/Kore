package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.Components
import io.github.ayfri.kore.generated.arguments.types.WolfVariantArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.arguments.components.ComponentsScope

@Serializable(with = WolfVariant.Companion.WolfVariantSerializer::class)
data class WolfVariant(
	var variant: WolfVariantArgument
) : Component() {
	companion object {
		data object WolfVariantSerializer : InlineAutoSerializer<WolfVariant>(WolfVariant::class)
	}
}

fun ComponentsScope.wolfVariant(variant: WolfVariantArgument) {
	this[EntityItemComponentTypes.WOLF_VARIANT] = WolfVariant(variant)
}
