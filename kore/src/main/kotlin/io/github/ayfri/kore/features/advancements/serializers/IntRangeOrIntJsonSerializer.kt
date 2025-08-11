package io.github.ayfri.kore.features.advancements.serializers

import io.github.ayfri.kore.arguments.numbers.ranges.IntRangeOrInt
import io.github.ayfri.kore.arguments.numbers.ranges.range
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrInt
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

typealias IntRangeOrIntJson = @Serializable(with = IntRangeOrIntJsonSerializer::class) IntRangeOrInt

data object IntRangeOrIntJsonSerializer : KSerializer<IntRangeOrInt> {
	override val descriptor = buildClassSerialDescriptor("IntRangeOrInt") {
		element<Int>("min")
		element<Int>("max")
	}

	override fun serialize(encoder: Encoder, value: IntRangeOrInt) = when {
		value.range != null -> encoder.encodeStructure(descriptor) {
			encodeIntElement(descriptor, 0, value.range.start!!)
			encodeIntElement(descriptor, 1, value.range.end!!)
		}

		else -> encoder.encodeInt(value.int!!)
	}

	@OptIn(ExperimentalSerializationApi::class)
	override fun deserialize(decoder: Decoder) = when {
		decoder.decodeNullableSerializableValue(Int.serializer().nullable) != null -> {
			decoder.decodeStructure(descriptor) {
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
