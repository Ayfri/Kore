package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import io.github.ayfri.kore.arguments.enums.MapDecoration as MapDecorationIcon

@Serializable
data class MapDecoration(
	var type: MapDecorationIcon,
	var x: Double,
	var z: Double,
	var rotation: Float,
)

@Serializable(with = MapDecorationsComponent.Companion.MapDecorationsComponentSerializer::class)
data class MapDecorationsComponent(var decorations: Map<String, MapDecoration>) : Component() {
	companion object {
		data object MapDecorationsComponentSerializer : InlineAutoSerializer<MapDecorationsComponent>(MapDecorationsComponent::class)
	}
}

fun ComponentsScope.mapDecorations(decorations: Map<String, MapDecoration>) =
	apply { this[ItemComponentTypes.MAP_DECORATIONS] = MapDecorationsComponent(decorations) }

fun ComponentsScope.mapDecorations(vararg decorations: Pair<String, MapDecoration>) =
	apply { this[ItemComponentTypes.MAP_DECORATIONS] = MapDecorationsComponent(decorations.toMap()) }

fun ComponentsScope.mapDecorations(block: MapDecorationsComponent.() -> Unit) =
	apply { this[ItemComponentTypes.MAP_DECORATIONS] = MapDecorationsComponent(emptyMap()).apply(block) }

fun MapDecorationsComponent.decoration(
	name: String,
	type: MapDecorationIcon,
	x: Double,
	z: Double,
	rotation: Float,
) = apply {
	decorations += name to MapDecoration(type, x, z, rotation)
}
