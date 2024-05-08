package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.serializers.NbtAsJsonSerializer
import io.github.ayfri.kore.serializers.ToStringSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Encoder
import net.benwoodworth.knbt.NbtTag

@Serializable(with = CustomComponent.Companion.CustomComponentSerializer::class)
abstract class CustomComponent : Component() {
	abstract val nbt: NbtTag

	companion object {
		object CustomComponentSerializer : ToStringSerializer<CustomComponent>() {
			override fun serialize(encoder: Encoder, value: CustomComponent) {
				encoder.encodeSerializableValue(NbtAsJsonSerializer, value.nbt)
			}
		}
	}
}
