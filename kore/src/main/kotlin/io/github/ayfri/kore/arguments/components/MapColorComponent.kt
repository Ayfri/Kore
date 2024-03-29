package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.arguments.colors.RGB
import io.github.ayfri.kore.generated.ComponentTypes
import io.github.ayfri.kore.serializers.InlineSerializer
import kotlinx.serialization.Serializable

@Serializable(with = MapColorComponent.Companion.MapColorComponentSerializer::class)
data class MapColorComponent(var color: RGB) : Component() {
	companion object {
		object MapColorComponentSerializer : InlineSerializer<MapColorComponent, RGB>(
			RGB.Companion.ColorAsDecimalSerializer,
			MapColorComponent::color
		)
	}
}

fun Components.mapColor(color: RGB) = apply { this[ComponentTypes.MAP_COLOR] = MapColorComponent(color) }
