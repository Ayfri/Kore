package io.github.ayfri.kore.arguments.colors

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Serializes a [Color] as a signed decimal INT 0xAARRGGBB, colors without alpha being fully opaque.
 *
 * Used where Minecraft expects an ARGB int, like the `shadow_color` of text components.
 * `Color.BLUE` is written as `-11184641` (`0xFF5555FF`).
 * See documentation: https://kore.ayfri.com/docs/concepts/colors
 */
data object ColorAsARGBDecimalSerializer : KSerializer<Color> {
	override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ARGBDecimalColor", PrimitiveKind.INT)

	override fun deserialize(decoder: Decoder) = ARGB.fromDecimal(decoder.decodeInt())

	override fun serialize(encoder: Encoder, value: Color) = encoder.encodeInt(value.toARGB().decimal)
}
