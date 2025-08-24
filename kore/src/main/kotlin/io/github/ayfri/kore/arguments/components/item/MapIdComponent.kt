package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = MapIdComponent.Companion.MapIdComponentSerializer::class)
data class MapIdComponent(var id: Int) : Component() {
	companion object {
		data object MapIdComponentSerializer : InlineAutoSerializer<MapIdComponent>(MapIdComponent::class)
	}
}

fun ComponentsScope.mapId(id: Int) = apply { this[ItemComponentTypes.MAP_ID] = MapIdComponent(id) }
