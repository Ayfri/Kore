package io.github.ayfri.kore.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

open class ToStringSerializer<T>(
	private val transform: T.() -> String = { toString() },
	private val fromString: ((String) -> T)? = null,
) : KSerializer<T> {
	override val descriptor = PrimitiveSerialDescriptor("ToStringSerializer", PrimitiveKind.STRING)

	override fun deserialize(decoder: Decoder): T =
		fromString?.invoke(decoder.decodeString())
			?: error("ToStringSerializer for this type does not support deserialization")

	override fun serialize(encoder: Encoder, value: T) = encoder.encodeString(value.transform())
}
