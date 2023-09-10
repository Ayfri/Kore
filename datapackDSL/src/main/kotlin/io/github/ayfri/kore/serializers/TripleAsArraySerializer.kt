package io.github.ayfri.kore.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeCollection

typealias TripleAsArray<A, B, C> = @Serializable(TripleAsArraySerializer::class) Triple<A, B, C>

class TripleAsArraySerializer<A, B, C>(
	private val firstSerializer: KSerializer<A>,
	private val secondSerializer: KSerializer<B>,
	private val thirdSerializer: KSerializer<C>,
) : KSerializer<Triple<A, B, C>> {
	override val descriptor = mixedListSerialDescriptor(firstSerializer, secondSerializer, thirdSerializer)

	override fun deserialize(decoder: Decoder) = error("TripleAsArray is not meant to be deserialized")

	override fun serialize(encoder: Encoder, value: Triple<A, B, C>) =
		encoder.encodeCollection(descriptor, 3) {
			encodeSerializableElement(descriptor, 0, firstSerializer, value.first)
			encodeSerializableElement(descriptor, 1, secondSerializer, value.second)
			encodeSerializableElement(descriptor, 2, thirdSerializer, value.third)
		}
}
