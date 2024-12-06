package io.github.ayfri.kore.arguments.colors

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.DoubleArraySerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

data object ColorAsDoubleArraySerializer : KSerializer<Color> {
	override val descriptor = DoubleArraySerializer().descriptor

	override fun deserialize(decoder: Decoder): Color {
		val array = decoder.decodeSerializableValue(DoubleArraySerializer())
		return RGB.fromDoubleArray(array)
	}

	override fun serialize(encoder: Encoder, value: Color) {
		encoder.encodeSerializableValue(DoubleArraySerializer(), value.toRGB().normalizedArray)
	}
}
