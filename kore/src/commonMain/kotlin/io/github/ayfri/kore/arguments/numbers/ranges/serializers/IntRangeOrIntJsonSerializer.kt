package io.github.ayfri.kore.arguments.numbers.ranges.serializers

import io.github.ayfri.kore.arguments.numbers.ranges.IntRange
import io.github.ayfri.kore.arguments.numbers.ranges.IntRangeOrInt
import io.github.ayfri.kore.arguments.numbers.ranges.rangeOrInt
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

typealias IntRangeOrIntJson = @Serializable(with = IntRangeOrIntJsonSerializer::class) IntRangeOrInt

data object IntRangeOrIntJsonSerializer : KSerializer<IntRangeOrInt> {
	override val descriptor = buildClassSerialDescriptor("IntRangeOrInt") {
		element<Int>("min")
		element<Int>("max")
	}

	override fun serialize(encoder: Encoder, value: IntRangeOrInt) = when {
		value.range != null -> encoder.encodeStructure(descriptor) {
			value.range.start?.let { encodeIntElement(descriptor, 0, it) }
			value.range.end?.let { encodeIntElement(descriptor, 1, it) }
		}

		else -> encoder.encodeInt(value.int!!)
	}

	@OptIn(ExperimentalSerializationApi::class)
	override fun deserialize(decoder: Decoder): IntRangeOrInt {
		val jsonDecoder =
			decoder as? kotlinx.serialization.json.JsonDecoder ?: error("This serializer can only be used with JSON")
		return when (val element = jsonDecoder.decodeJsonElement()) {
			is JsonPrimitive -> {
				rangeOrInt(element.content.toInt())
			}

			is JsonObject -> {
				val min = element["min"]?.let { (it as JsonPrimitive).content.toInt() }
				val max = element["max"]?.let { (it as JsonPrimitive).content.toInt() }
				IntRangeOrInt(IntRange(min, max))
			}

			else -> error("Unexpected JSON element: $element")
		}
	}
}
