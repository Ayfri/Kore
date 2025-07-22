package io.github.ayfri.kore.arguments.components.entity

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.Components
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.enums.DyeColors
import io.github.ayfri.kore.generated.EntityItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = ShulkerColor.Companion.ShulkerColorSerializer::class)
data class ShulkerColor(
	var color: DyeColors
) : Component() {
	companion object {
		data object ShulkerColorSerializer : InlineAutoSerializer<ShulkerColor>(ShulkerColor::class)
	}
}

fun ComponentsScope.shulkerColor(color: DyeColors) {
	this[EntityItemComponentTypes.SHULKER_COLOR] = ShulkerColor(color)
}
