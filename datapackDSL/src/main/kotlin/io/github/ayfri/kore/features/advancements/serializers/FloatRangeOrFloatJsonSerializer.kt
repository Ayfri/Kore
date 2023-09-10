package io.github.ayfri.kore.features.advancements.serializers

import io.github.ayfri.kore.arguments.numbers.ranges.FloatRangeOrFloat
import io.github.ayfri.kore.arguments.numbers.ranges.range
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrDouble
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

typealias FloatRangeOrFloatJson = @Serializable(with = FloatRangeOrFloatJsonSerializer::class) FloatRangeOrFloat

object FloatRangeOrFloatJsonSerializer : KSerializer<FloatRangeOrFloat> {
	override val descriptor = buildClassSerialDescriptor("IntRangeOrInt") {
		element<Double>("max")
		element<Double>("min")
	}

	override fun serialize(encoder: Encoder, value: FloatRangeOrFloat) = when {
		value.range != null -> encoder.encodeStructure(descriptor) {
			encodeDoubleElement(PrimitiveSerialDescriptor("min", PrimitiveKind.DOUBLE), 0, value.double!!)
			encodeDoubleElement(PrimitiveSerialDescriptor("max", PrimitiveKind.DOUBLE), 1, value.double)
		}

		else -> encoder.encodeDouble(value.double!!)
	}

	@OptIn(ExperimentalSerializationApi::class)
	override fun deserialize(decoder: Decoder) = when {
		decoder.decodeNullableSerializableValue(Double.serializer().nullable) != null -> {
			decoder.decodeStructure(descriptor) {
				var min: Double? = null
				var max: Double? = null

				while (true) {
					when (val index = decodeElementIndex(descriptor)) {
						0 -> min = decodeDoubleElement(PrimitiveSerialDescriptor("min", PrimitiveKind.DOUBLE), 0)
						1 -> max = decodeDoubleElement(PrimitiveSerialDescriptor("max", PrimitiveKind.DOUBLE), 1)
						-1 -> break
						else -> error("Unexpected index: $index")
					}
				}

				rangeOrDouble(range(min!!, max!!))
			}
		}

		else -> rangeOrDouble(decoder.decodeDouble())
	}
}
