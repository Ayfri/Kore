package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.arguments.components.item.CustomComponent
import io.github.ayfri.kore.serializers.GeneratedSerializerMap
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonEncoder
import net.benwoodworth.knbt.NbtEncoder
import net.benwoodworth.knbt.NbtTag

@GeneratedSerializerMap
@Serializable(with = Component.Companion.ComponentSerializer::class)
abstract class Component {
	companion object {
		class ComponentSerializer : KSerializer<Component> {
			val kClass = Component::class

			override val descriptor = serialDescriptor<NbtTag>()

			override fun deserialize(decoder: Decoder) = error("${kClass.simpleName} cannot be deserialized")

			@Suppress("UNCHECKED_CAST")
			override fun serialize(encoder: Encoder, value: Component) {
				require(kClass.isInstance(value) && value::class != kClass) { "Value must be instance of ${kClass.simpleName}" }
				require(encoder is NbtEncoder || encoder is JsonEncoder) { "Components can only be serialized to Nbt or Json" }

				encoder.encodeSerializableValue(componentSerializerFor(value), value)
			}
		}
	}
}

/** Looks up the serializer of a component's runtime type, user subclasses of [CustomComponent] falling back to its serializer. */
@Suppress("UNCHECKED_CAST")
internal fun componentSerializerFor(value: Component) = (componentSerializers[value::class] ?: when (value) {
	is CustomComponent -> CustomComponent.Companion.CustomComponentSerializer
	else -> error("No serializer for component ${value::class.simpleName}, extend CustomComponent for components Kore doesn't ship.")
}) as KSerializer<Component>
