package io.github.ayfri.kore.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.*

typealias PairAsArray<A, B> = @Serializable(PairAsArraySerializer::class) Pair<A, B>

class PairAsArraySerializer<A, B>(
	private val firstSerializer: KSerializer<A>,
	private val secondSerializer: KSerializer<B>,
) : KSerializer<Pair<A, B>> {
	override val descriptor = mixedListSerialDescriptor(firstSerializer, secondSerializer)

	@Suppress("UNCHECKED_CAST")
	override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
		var first = null as A
		var second = null as B
		while (true) {
			when (val index = decodeElementIndex(descriptor)) {
				0 -> first = decodeSerializableElement(descriptor, 0, firstSerializer)
				1 -> second = decodeSerializableElement(descriptor, 1, secondSerializer)
				CompositeDecoder.DECODE_DONE -> break
				else -> error("Unexpected index $index in PairAsArray")
			}
		}
		Pair(first, second)
	}

	override fun serialize(encoder: Encoder, value: Pair<A, B>) =
		encoder.encodeCollection(descriptor, 2) {
			encodeSerializableElement(descriptor, 0, firstSerializer, value.first)
			encodeSerializableElement(descriptor, 1, secondSerializer, value.second)
		}
}
