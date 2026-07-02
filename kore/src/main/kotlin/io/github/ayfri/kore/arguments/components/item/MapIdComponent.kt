package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/**
 * Represents the `minecraft:map_id` item component, which links the item to a specific map data ID for filled maps.
 *
 * Serializes as the integer id directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#map_id
 */
@Serializable(with = MapIdComponent.Companion.MapIdComponentSerializer::class)
data class MapIdComponent(var id: Int) : Component() {
	companion object {
		data object MapIdComponentSerializer :
			InlineAutoSerializer<MapIdComponent, Int>(serializer<Int>(), MapIdComponent::id, ::MapIdComponent)
	}
}

/** Links the item to a specific map data ID for filled maps. */
fun ComponentsScope.mapId(id: Int) = apply { this[ItemComponentTypes.MAP_ID] = MapIdComponent(id) }
