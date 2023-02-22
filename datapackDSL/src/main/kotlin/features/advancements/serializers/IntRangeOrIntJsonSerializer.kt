package features.advancements.serializers

import arguments.numbers.IntRangeOrInt
import arguments.numbers.range
import arguments.numbers.rangeOrInt
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import serializers.ToStringSerializer

typealias IntRangeOrIntJson = @Serializable(with = IntRangeOrIntJsonSerializer::class) IntRangeOrInt

object IntRangeOrIntJsonSerializer : ToStringSerializer<IntRangeOrInt>() {
	private val serialDescriptor =
		buildClassSerialDescriptor("IntRangeOrInt") {
			element<Int>("max")
			element<Int>("min")
		}

	override fun serialize(encoder: Encoder, value: IntRangeOrInt) = when {
		value.range != null -> encoder.encodeStructure(serialDescriptor) {
			encodeIntElement(PrimitiveSerialDescriptor("min", PrimitiveKind.INT), 0, value.int!!)
			encodeIntElement(PrimitiveSerialDescriptor("max", PrimitiveKind.INT), 1, value.int)
		}

		else -> encoder.encodeInt(value.int!!)
	}

	@OptIn(ExperimentalSerializationApi::class)
	override fun deserialize(decoder: Decoder) = when {
		decoder.decodeNullableSerializableValue(Int.serializer().nullable) != null -> {
			decoder.decodeStructure(serialDescriptor) {
				var min: Int? = null
				var max: Int? = null

				while (true) {
					when (val index = decodeElementIndex(descriptor)) {
						0 -> min = decodeIntElement(PrimitiveSerialDescriptor("min", PrimitiveKind.INT), 0)
						1 -> max = decodeIntElement(PrimitiveSerialDescriptor("max", PrimitiveKind.INT), 1)
						-1 -> break
						else -> error("Unexpected index: $index")
					}
				}

				rangeOrInt(range(min!!, max!!))
			}
		}

		else -> rangeOrInt(decoder.decodeInt())
	}
}
