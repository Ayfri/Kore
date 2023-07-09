package serializers

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.internal.AbstractPolymorphicSerializer

/**
 * Serializes a polymorphic value with its concrete serializer directly,
 * instead of e.g. serializing an extra "type" discriminator entry.
 *
 * Only encoding is supported, and the [serializer] must be the default/generated polymorphic serializer.
 */
@OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
class DirectPolymorphicSerializer<T : Any>(serializer: KSerializer<T>) : KSerializer<T> {
	private val serializer = run {
		require(serializer is AbstractPolymorphicSerializer<T>) {
			"Serializer must be a ${AbstractPolymorphicSerializer::class.simpleName}"
		}
		serializer
	}

	/**
	 * PolymorphicSerializer and SealedClassSerializer (the only implementors of AbstractPolymorphicSerializer within kotlinx.serialization),
	 * both have their polymorphic descriptor element named `value`.
	 */
	private val valueDescriptor = serializer.descriptor.getElementDescriptor(serializer.descriptor.getElementIndex("value"))

	override val descriptor = SerialDescriptor("${DirectPolymorphicSerializer::class}<${this.serializer.baseClass}>", valueDescriptor)

	override fun serialize(encoder: Encoder, value: T) {
		val directActualSerializer = when (val actualSerializer = serializer.findPolymorphicSerializer(encoder, value)) {
			is AbstractPolymorphicSerializer -> DirectPolymorphicSerializer(actualSerializer)
			else -> actualSerializer
		}

		encoder.encodeSerializableValue(directActualSerializer, value)
	}

	@Throws(SerializationException::class)
	override fun deserialize(decoder: Decoder) =
		throw SerializationException("${DirectPolymorphicSerializer::class.simpleName} only supports encoding")
}
