package io.github.ayfri.kore.serializers

import kotlin.enums.EnumEntries
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

open class EnumOrdinalSerializer<T>(private val values: EnumEntries<T>) : KSerializer<T> where T : Enum<T> {
	override val descriptor = PrimitiveSerialDescriptor("EnumOrdinalSerializer", PrimitiveKind.INT)

	override fun deserialize(decoder: Decoder): T {
		val value = decoder.decodeInt()
		return values[value]
	}

	override fun serialize(encoder: Encoder, value: T) = encoder.encodeInt(value.ordinal)
}
