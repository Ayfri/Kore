package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.colors.RGB
import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = MapColorComponent.Companion.MapColorComponentSerializer::class)
data class MapColorComponent(
	@Serializable(RGB.Companion.ColorAsDecimalSerializer::class) var color: RGB,
) : Component() {
	companion object {
		data object MapColorComponentSerializer : InlineAutoSerializer<MapColorComponent>(MapColorComponent::class)
	}
}

fun ComponentsScope.mapColor(color: RGB) = apply { this[ItemComponentTypes.MAP_COLOR] = MapColorComponent(color) }
