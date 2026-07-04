package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.MapDecorationTypeArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/** A single named map icon/marker displayed on a filled map. */
@Serializable
data class MapDecoration(
	var type: MapDecorationTypeArgument,
	var x: Double,
	var z: Double,
	var rotation: Float,
)

/**
 * Represents the `minecraft:map_decorations` item component, which adds custom icons/markers displayed on a filled map.
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#map_decorations
 */
@Serializable(with = MapDecorationsComponent.Companion.MapDecorationsComponentSerializer::class)
data class MapDecorationsComponent(var decorations: Map<String, MapDecoration>) : Component() {
	companion object {
		data object MapDecorationsComponentSerializer :
			InlineAutoSerializer<MapDecorationsComponent, Map<String, MapDecoration>>(
				serializer<Map<String, MapDecoration>>(),
				MapDecorationsComponent::decorations,
				::MapDecorationsComponent
			)
	}
}

/** Adds custom icons/markers displayed on a filled map. */
fun ComponentsScope.mapDecorations(decorations: Map<String, MapDecoration>) =
	apply { this[ItemComponentTypes.MAP_DECORATIONS] = MapDecorationsComponent(decorations) }

fun ComponentsScope.mapDecorations(vararg decorations: Pair<String, MapDecoration>) =
	apply { this[ItemComponentTypes.MAP_DECORATIONS] = MapDecorationsComponent(decorations.toMap()) }

fun ComponentsScope.mapDecorations(block: MapDecorationsComponent.() -> Unit) =
	apply { this[ItemComponentTypes.MAP_DECORATIONS] = MapDecorationsComponent(emptyMap()).apply(block) }

fun MapDecorationsComponent.decoration(
	name: String,
	type: MapDecorationTypeArgument,
	x: Double,
	z: Double,
	rotation: Float,
) = apply {
	decorations += name to MapDecoration(type, x, z, rotation)
}
