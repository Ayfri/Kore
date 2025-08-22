package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.colors.RGB
import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = DyedColorComponent.Companion.DyedColorComponentSerializer::class)
data class DyedColorComponent(@Serializable(RGB.Companion.ColorAsDecimalSerializer::class) var rgb: RGB) : Component() {
	companion object {
		data object DyedColorComponentSerializer : InlineAutoSerializer<DyedColorComponent>(DyedColorComponent::class)
	}
}

fun ComponentsScope.dyedColor(rgb: Color) =
	apply { this[ItemComponentTypes.DYED_COLOR] = DyedColorComponent(rgb.toRGB()) }
