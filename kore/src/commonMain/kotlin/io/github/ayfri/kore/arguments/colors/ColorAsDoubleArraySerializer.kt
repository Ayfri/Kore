package io.github.ayfri.kore.arguments.colors

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.DoubleArraySerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Serializes a [Color] as a `[r, g, b]` double-array with values in 0.0..1.0.
 *
 * Used by some particle effect payloads (e.g., enchantment-related) that expect normalized colors.
 * See documentation: https://kore.ayfri.com/docs/concepts/colors
 */
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
