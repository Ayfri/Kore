package io.github.ayfri.kore.arguments.components

import kotlin.reflect.full.createType
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.serializer

@Serializable(with = Component.Companion.ComponentSerializer::class)
sealed class Component {
	companion object {
		class ComponentSerializer : KSerializer<Component> {
			val kClass = Component::class

			override val descriptor = serialDescriptor<JsonElement>()

			override fun deserialize(decoder: Decoder) = error("${kClass.simpleName} cannot be deserialized")

			@OptIn(ExperimentalSerializationApi::class)
			override fun serialize(encoder: Encoder, value: Component) {
				require(encoder is JsonEncoder) { "PolymorphicTypeSerializer can only be serialized to Json" }
				require(kClass.isInstance(value) && value::class != kClass) { "Value must be instance of ${kClass.simpleName}" }

				val serializer = encoder.serializersModule.getPolymorphic(kClass, value)
					?: encoder.serializersModule.getContextual(value::class)
					?: encoder.serializersModule.serializer(value::class.createType())

				val valueJson = encoder.json.encodeToJsonElement(serializer as KSerializer<Component>, value)

				encoder.encodeJsonElement(valueJson)
			}
		}
	}
}
