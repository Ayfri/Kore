package io.github.ayfri.kore.features.worldgen.dimension.biomesource.multinoise

import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer

@Serializable(with = DoubleOrDoublePair.Companion.DoubleOrDoublePairSerializer::class)
data class DoubleOrDoublePair(
	val number: Double? = null,
	val pair: Pair<Double, Double>? = null
) {
	companion object {
		object DoubleOrDoublePairSerializer : kotlinx.serialization.KSerializer<DoubleOrDoublePair> {
			override val descriptor = buildClassSerialDescriptor("DoubleOrDoublePair")

			override fun deserialize(decoder: Decoder) = error("DoubleOrDoublePair is not deserializable")

			override fun serialize(encoder: Encoder, value: DoubleOrDoublePair) = when {
				value.number != null -> encoder.encodeDouble(value.number)
				value.pair != null -> encoder.encodeSerializableValue(
					serializer<Pair<Double, Double>>(),
					value.pair
				)

				else -> error("DoubleOrDoublePair must have either a number or a pair")
			}
		}
	}
}

fun doubleOrPair(number: Double) = DoubleOrDoublePair(number = number)
fun doubleOrPair(pair: Pair<Double, Double>) = DoubleOrDoublePair(pair = pair)
fun doubleOrPair(first: Double, second: Double) = DoubleOrDoublePair(pair = first to second)
