package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ComponentTypes
import io.github.ayfri.kore.serializers.InlineSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
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
		object MapDecorationsComponentSerializer : InlineSerializer<MapDecorationsComponent, Map<String, MapDecoration>>(
			MapSerializer(
				String.serializer(),
				MapDecoration.serializer()
			),
			MapDecorationsComponent::decorations
		)
	}
}

fun ComponentsScope.mapDecorations(decorations: Map<String, MapDecoration>) =
	apply { this[ComponentTypes.MAP_DECORATIONS] = MapDecorationsComponent(decorations) }

fun ComponentsScope.mapDecorations(vararg decorations: Pair<String, MapDecoration>) =
	apply { this[ComponentTypes.MAP_DECORATIONS] = MapDecorationsComponent(decorations.toMap()) }

fun ComponentsScope.mapDecorations(block: MapDecorationsComponent.() -> Unit) =
	apply { this[ComponentTypes.MAP_DECORATIONS] = MapDecorationsComponent(emptyMap()).apply(block) }

fun MapDecorationsComponent.decoration(
	name: String,
	type: MapDecorationIcon,
	x: Double,
	z: Double,
	rotation: Float,
) = apply {
	decorations += name to MapDecoration(type, x, z, rotation)
}
