package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.colors.RGB
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DyedColorComponent(
	@Serializable(RGB.Companion.ColorAsDecimalSerializer::class) var rgb: RGB,
	@SerialName("show_in_tooltip")
	val showInTooltip: Boolean? = null,
) : Component()

fun Components.dyedColor(rgb: Color, showInTooltip: Boolean? = null) =
	apply { components["dyed_color"] = DyedColorComponent(rgb.toRGB(), showInTooltip) }
