package io.github.ayfri.kore.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.*

typealias TripleAsArray<A, B, C> = @Serializable(TripleAsArraySerializer::class) Triple<A, B, C>

class TripleAsArraySerializer<A, B, C>(
	private val firstSerializer: KSerializer<A>,
	private val secondSerializer: KSerializer<B>,
	private val thirdSerializer: KSerializer<C>,
) : KSerializer<Triple<A, B, C>> {
	override val descriptor = mixedListSerialDescriptor(firstSerializer, secondSerializer, thirdSerializer)

	@Suppress("UNCHECKED_CAST")
	override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
		var first = null as A
		var second = null as B
		var third = null as C
		while (true) {
			when (val index = decodeElementIndex(descriptor)) {
				0 -> first = decodeSerializableElement(descriptor, 0, firstSerializer)
				1 -> second = decodeSerializableElement(descriptor, 1, secondSerializer)
				2 -> third = decodeSerializableElement(descriptor, 2, thirdSerializer)
				CompositeDecoder.DECODE_DONE -> break
				else -> error("Unexpected index $index in TripleAsArray")
			}
		}
		Triple(first, second, third)
	}

	override fun serialize(encoder: Encoder, value: Triple<A, B, C>) =
		encoder.encodeCollection(descriptor, 3) {
			encodeSerializableElement(descriptor, 0, firstSerializer, value.first)
			encodeSerializableElement(descriptor, 1, secondSerializer, value.second)
			encodeSerializableElement(descriptor, 2, thirdSerializer, value.third)
		}
}
