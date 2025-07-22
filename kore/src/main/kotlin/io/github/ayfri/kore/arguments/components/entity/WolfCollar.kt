package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.Components
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.enums.DyeColors
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = WolfCollar.Companion.WolfCollarSerializer::class)
data class WolfCollar(
	var color: DyeColors
) : Component() {
	companion object {
		data object WolfCollarSerializer : InlineAutoSerializer<WolfCollar>(WolfCollar::class)
	}
}

fun ComponentsScope.wolfCollar(color: DyeColors) {
	this[EntityItemComponentTypes.WOLF_COLLAR] = WolfCollar(color)
}
