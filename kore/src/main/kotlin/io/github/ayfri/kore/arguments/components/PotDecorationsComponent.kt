package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.arguments.types.resources.ItemArgument
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

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

fun Components.potDecorations(decorations: List<ItemArgument>) = apply {
	components["pot_decorations"] = PotDecorationsComponent(decorations)
}

fun Components.potDecorations(vararg decorations: ItemArgument) = apply {
	components["pot_decorations"] = PotDecorationsComponent(decorations.toList())
}
