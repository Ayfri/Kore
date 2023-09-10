package io.github.ayfri.kore.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

open class ToStringSerializer<T>(val transform: (T.(Encoder) -> String)? = null) : KSerializer<T> {
	override val descriptor = PrimitiveSerialDescriptor("ToStringSerializer", PrimitiveKind.STRING)

	override fun deserialize(decoder: Decoder): T = error("ToStringSerializer is not meant to be deserialized")

	override fun serialize(encoder: Encoder, value: T) = encoder.encodeString(transform?.let { value.it(encoder) } ?: value.toString())
}
