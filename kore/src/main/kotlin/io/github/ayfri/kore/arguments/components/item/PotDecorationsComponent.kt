package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import io.github.ayfri.kore.generated.ItemComponentTypes
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Represents the `minecraft:pot_decorations` item component, which defines the pottery sherds or bricks on each face of a decorated pot.
 *
 * Must contain exactly 4 items (one per face).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#pot_decorations
 */
@Serializable(with = PotDecorationsComponent.Companion.PotDecorationsComponentSerializer::class)
data class PotDecorationsComponent(var list: List<ItemArgument>) : Component() {
	companion object {
		object PotDecorationsComponentSerializer : KSerializer<PotDecorationsComponent> {
			override val descriptor = ListSerializer(ItemArgument.serializer()).descriptor

			override fun deserialize(decoder: Decoder) = error("PotDecorationsComponent is not deserializable.")

			override fun serialize(encoder: Encoder, value: PotDecorationsComponent) {
				require(value.list.size == 4) { "Pot decorations must have exactly 4 items." }

				encoder.encodeSerializableValue(ListSerializer(String.serializer()), value.list.map { it.asId() })
			}
		}
	}
}

/** Defines the pottery sherds or bricks on each face of a decorated pot (must be exactly 4 items). */
fun ComponentsScope.potDecorations(decorations: List<ItemArgument>) = apply {
	this[ItemComponentTypes.POT_DECORATIONS] = PotDecorationsComponent(decorations)
}

fun ComponentsScope.potDecorations(vararg decorations: ItemArgument) = apply {
	this[ItemComponentTypes.POT_DECORATIONS] = PotDecorationsComponent(decorations.toList())
}
