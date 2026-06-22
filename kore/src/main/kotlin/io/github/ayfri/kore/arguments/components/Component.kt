package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.utils.serializerFor
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonEncoder
import net.benwoodworth.knbt.NbtEncoder
import net.benwoodworth.knbt.NbtTag

@Serializable(with = Component.Companion.ComponentSerializer::class)
abstract class Component {
	/**
	 * Whether this component wraps a chat component, so its SNBT value must be unescaped (and JSON ones single-quoted)
	 * when rendered inside the `item[key=value]` command syntax. See [unescapeChatComponent].
	 *
	 * Defined as a function (not a property) so it is never picked up as a serializable field.
	 */
	open fun isChatComponent() = false

	companion object {
		class ComponentSerializer : KSerializer<Component> {
			val kClass = Component::class

			override val descriptor = serialDescriptor<NbtTag>()

			override fun deserialize(decoder: Decoder) = error("${kClass.simpleName} cannot be deserialized")

			override fun serialize(encoder: Encoder, value: Component) {
				require(kClass.isInstance(value) && value::class != kClass) { "Value must be instance of ${kClass.simpleName}" }

				val serializer = encoder.serializersModule.serializerFor(value)

				when (encoder) {
					is NbtEncoder -> {
						val valueNbt = encoder.nbt.encodeToNbtTag(serializer, value)
						encoder.encodeSerializableValue(NbtTag.serializer(), valueNbt)
					}

					is JsonEncoder -> {
						val valueJson = encoder.json.encodeToJsonElement(serializer, value)
						encoder.encodeJsonElement(valueJson)
					}

					else -> error("Components can only be serialized to Nbt or Json")
				}
			}
		}
	}
}
