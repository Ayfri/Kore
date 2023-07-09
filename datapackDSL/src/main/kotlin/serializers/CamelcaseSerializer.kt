package serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.*
import kotlin.enums.EnumEntries

private fun String.camelcase(): String {
	val words = lowercase().split("_")
	return words[0] + words.drop(1).joinToString("") { word ->
		word.replaceFirstChar { it.titlecase(Locale.ENGLISH) }
	}
}

open class CamelcaseSerializer<T>(private val values: EnumEntries<T>) : KSerializer<T> where T : Enum<T> {
	override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("CamelcaseSerializer", PrimitiveKind.STRING)

	override fun deserialize(decoder: Decoder): T {
		val value = decoder.decodeString()
		return values.first { it.name.camelcase() == value }
	}

	override fun serialize(encoder: Encoder, value: T) {
		encoder.encodeString(value.name.camelcase())
	}
}
