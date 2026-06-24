package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.colors.RGB
import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

/**
 * Represents the `minecraft:map_color` item component, which sets the color tint for filled map item textures.
 *
 * Serializes as the decimal RGB integer directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#map_color
 */
@Serializable(with = MapColorComponent.Companion.MapColorComponentSerializer::class)
data class MapColorComponent(
	@Serializable(RGB.Companion.ColorAsDecimalSerializer::class) var color: RGB,
) : Component() {
	companion object {
		data object MapColorComponentSerializer : InlineAutoSerializer<MapColorComponent>(MapColorComponent::class)
	}
}

/** Sets the color tint for filled map item textures. */
fun ComponentsScope.mapColor(color: RGB) = apply { this[ItemComponentTypes.MAP_COLOR] = MapColorComponent(color) }
