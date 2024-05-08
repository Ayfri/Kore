package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.colors.RGB
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ComponentTypes
import io.github.ayfri.kore.serializers.SinglePropertySimplifierSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable(with = DyedColorComponent.Companion.DyedColorComponentSerializer::class)
data class DyedColorComponent(
	@Serializable(RGB.Companion.ColorAsDecimalSerializer::class) var rgb: RGB,
	@SerialName("show_in_tooltip")
	val showInTooltip: Boolean? = null,
) : Component() {
	companion object {
		data object DyedColorComponentSerializer : SinglePropertySimplifierSerializer<DyedColorComponent, RGB>(
			DyedColorComponent::class,
			DyedColorComponent::rgb,
		)
	}
}

fun ComponentsScope.dyedColor(rgb: Color, showInTooltip: Boolean? = null) =
	apply { this[ComponentTypes.DYED_COLOR] = DyedColorComponent(rgb.toRGB(), showInTooltip) }
