package io.github.ayfri.kore.arguments.colors

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.DoubleArraySerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Serializes a [Color] as a `[r, g, b, a]` double-array with values in 0.0..1.0, colors without alpha being fully opaque.
 *
 * Used where Minecraft expects normalized RGBA colors, like the chat type style.
 * See documentation: https://kore.ayfri.com/docs/concepts/colors
 */
data object ColorAsDoubleArrayRGBASerializer : KSerializer<Color> {
	override val descriptor = DoubleArraySerializer().descriptor

	override fun deserialize(decoder: Decoder): Color {
		val array = decoder.decodeSerializableValue(DoubleArraySerializer())
		return ARGB.fromRGBAArray(array)
	}

	override fun serialize(encoder: Encoder, value: Color) {
		encoder.encodeSerializableValue(DoubleArraySerializer(), value.toARGB().rgbaNormalizedArray)
	}
}
