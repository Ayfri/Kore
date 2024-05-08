package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ComponentTypes
import io.github.ayfri.kore.serializers.InlineSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer

@Serializable(with = MapIdComponent.Companion.MapIdComponentSerializer::class)
data class MapIdComponent(var id: Int) : Component() {
	companion object {
		object MapIdComponentSerializer : InlineSerializer<MapIdComponent, Int>(Int.serializer(), MapIdComponent::id)
	}
}

fun ComponentsScope.mapId(id: Int) = apply { this[ComponentTypes.MAP_ID] = MapIdComponent(id) }
