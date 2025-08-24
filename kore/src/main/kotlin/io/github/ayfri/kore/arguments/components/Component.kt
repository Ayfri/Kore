package io.github.ayfri.kore.arguments.components

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonEncoder
import net.benwoodworth.knbt.NbtEncoder
import net.benwoodworth.knbt.NbtTag
import kotlin.reflect.full.createType

@Serializable(with = Component.Companion.ComponentSerializer::class)
abstract class Component {
	companion object {
		class ComponentSerializer : KSerializer<Component> {
			val kClass = Component::class

			override val descriptor = serialDescriptor<NbtTag>()

			override fun deserialize(decoder: Decoder) = error("${kClass.simpleName} cannot be deserialized")

			@OptIn(ExperimentalSerializationApi::class)
			override fun serialize(encoder: Encoder, value: Component) {
				require(kClass.isInstance(value) && value::class != kClass) { "Value must be instance of ${kClass.simpleName}" }

				val defaultSerializer = encoder.serializersModule.serializer(value::class.createType()) as SerializationStrategy<Component>
				val polymorphic = encoder.serializersModule.getPolymorphic(kClass, value)
				val contextual = encoder.serializersModule.getContextual(value::class) as? KSerializer<Component>?
				val serializer = polymorphic ?: contextual ?: defaultSerializer

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
