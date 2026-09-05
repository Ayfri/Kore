package io.github.ayfri.kore.arguments.numbers.ranges.serializers

import io.github.ayfri.kore.arguments.numbers.ranges.FloatRange
import io.github.ayfri.kore.arguments.numbers.ranges.FloatRangeOrFloat
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrDouble
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

typealias FloatRangeOrFloatJson = @Serializable(with = FloatRangeOrFloatJsonSerializer::class) FloatRangeOrFloat

/** Writes a [FloatRangeOrFloat] as a bare number, or as `{ "min": ..., "max": ... }` with either bound left out when open. */
data object FloatRangeOrFloatJsonSerializer : KSerializer<FloatRangeOrFloat> {
	override val descriptor = buildClassSerialDescriptor("FloatRangeOrFloat") {
		element<Double>("min")
		element<Double>("max")
	}

	override fun serialize(encoder: Encoder, value: FloatRangeOrFloat) = when {
		value.range != null -> encoder.encodeStructure(descriptor) {
			value.range.start?.let { encodeDoubleElement(descriptor, 0, it) }
			value.range.end?.let { encodeDoubleElement(descriptor, 1, it) }
		}

		else -> encoder.encodeDouble(value.double!!)
	}

	override fun deserialize(decoder: Decoder): FloatRangeOrFloat {
		val jsonDecoder = decoder as? JsonDecoder ?: error("This serializer can only be used with JSON")
		return when (val element = jsonDecoder.decodeJsonElement()) {
			is JsonPrimitive -> rangeOrDouble(element.content.toDouble())
			is JsonObject -> FloatRangeOrFloat(
				FloatRange(
					(element["min"] as? JsonPrimitive)?.content?.toDouble(),
					(element["max"] as? JsonPrimitive)?.content?.toDouble(),
				)
			)

			else -> error("Unexpected JSON element: $element")
		}
	}
}
