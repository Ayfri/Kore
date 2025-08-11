package io.github.ayfri.kore.arguments.colors

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Serializes a [Color] as a decimal INT 0xRRGGBB.
 *
 * Used where Minecraft expects numeric colors (components like dyed color, fireworks, biome colors, particles via commands).
 * See documentation: https://kore.ayfri.com/docs/colors
 */
data object ColorAsDecimalSerializer : KSerializer<Color> {
	override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Color", PrimitiveKind.STRING)

	override fun deserialize(decoder: Decoder) = RGB.fromDecimal(decoder.decodeInt())

	override fun serialize(encoder: Encoder, value: Color) =
		encoder.encodeSerializableValue(RGB.Companion.ColorAsDecimalSerializer, value.toRGB())
}
