package io.github.ayfri.kore.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.enums.EnumEntries

open class LowercaseSerializer<T>(private val values: EnumEntries<T>, val transform: T.(Encoder) -> String = { name.lowercase() }) : KSerializer<T> where T : Enum<T> {
	override val descriptor = PrimitiveSerialDescriptor("LowercaseSerializer", PrimitiveKind.STRING)

	override fun deserialize(decoder: Decoder): T {
		val value = decoder.decodeString()
		return values.first { it.name.lowercase() == value }
	}

	override fun serialize(encoder: Encoder, value: T) = encoder.encodeString(transform(value, encoder))
}
