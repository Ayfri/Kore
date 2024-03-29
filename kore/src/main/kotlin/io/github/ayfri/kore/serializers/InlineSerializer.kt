package io.github.ayfri.kore.serializers

import kotlin.reflect.KProperty1
import net.benwoodworth.knbt.NbtTag
import kotlinx.serialization.KSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

open class InlineSerializer<T, P>(
	private val kSerializer: KSerializer<P>,
	private val property: KProperty1<T, P>,
) : KSerializer<T> {
	override val descriptor = NbtTag.serializer().descriptor

	override fun deserialize(decoder: Decoder) = error("InlineSerializer cannot be deserialized")

	override fun serialize(encoder: Encoder, value: T) = encoder.encodeSerializableValue(kSerializer, property.get(value))
}
