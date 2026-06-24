package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.arguments.colors.RGB
import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:dyed_color` item component, which sets the dye color for leather armor or other dyeable items.
 *
 * The color is stored as an RGB decimal integer. Serializes as the color value directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#dyed_color
 */
@Serializable(with = DyedColorComponent.Companion.DyedColorComponentSerializer::class)
data class DyedColorComponent(@Serializable(RGB.Companion.ColorAsDecimalSerializer::class) var rgb: RGB) : Component() {
	companion object {
		data object DyedColorComponentSerializer : InlineAutoSerializer<DyedColorComponent>(DyedColorComponent::class)
	}
}

/** Sets the dye color for leather armor or other dyeable items. */
fun ComponentsScope.dyedColor(rgb: Color) =
	apply { this[ItemComponentTypes.DYED_COLOR] = DyedColorComponent(rgb.toRGB()) }
