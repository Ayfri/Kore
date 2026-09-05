package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.serializers.NbtAsJsonSerializer
import io.github.ayfri.kore.serializers.ToStringSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Encoder
import net.benwoodworth.knbt.NbtEncoder
import net.benwoodworth.knbt.NbtTag

/**
 * Base class for user-defined item components backed by a raw [nbt] tag.
 *
 * Extend this to model components Kore does not ship a helper for. Serializes the [nbt] directly (as NBT in commands, JSON elsewhere).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format
 */
@Serializable(with = CustomComponent.Companion.CustomComponentSerializer::class)
open class CustomComponent(open val nbt: NbtTag) : Component() {
	override fun toString() = "CustomComponent(nbt=$nbt)"

	fun copy(nbt: NbtTag = this.nbt) = CustomComponent(nbt)

	operator fun component1() = nbt

	companion object {
		object CustomComponentSerializer : ToStringSerializer<CustomComponent>() {
			override fun serialize(encoder: Encoder, value: CustomComponent) = when (encoder) {
				is NbtEncoder -> encoder.encodeSerializableValue(NbtTag.serializer(), value.nbt)
				else -> encoder.encodeSerializableValue(NbtAsJsonSerializer, value.nbt)
			}
		}
	}
}
