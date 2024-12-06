package io.github.ayfri.kore.arguments.colors

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

data object ColorAsDecimalSerializer : KSerializer<Color> {
	override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Color", PrimitiveKind.STRING)

	override fun deserialize(decoder: Decoder) = RGB.fromDecimal(decoder.decodeInt())

	override fun serialize(encoder: Encoder, value: Color) =
		encoder.encodeSerializableValue(RGB.Companion.ColorAsDecimalSerializer, value.toRGB())
}
