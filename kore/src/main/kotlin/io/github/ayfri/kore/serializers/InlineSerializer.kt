package io.github.ayfri.kore.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * A serializer that serializes a property of a class.
 *
 * @param T The class that contains the property.
 * @param P The type of the property.
 * @param kSerializer The serializer of the property.
 * @param property Getter for the property.
 * @param factory Function building [T] from the property value.
 *
 * Example:
 * ```kotlin
 * data object MyDataClassSerializer : InlineSerializer<MyDataClass, Int>(
 *     kSerializer = Int.serializer(),
 *     property = MyDataClass::myProperty,
 *     factory = ::MyDataClass,
 * )
 *
 * @Serializable(with = MyDataClassSerializer::class)
 * data class MyDataClass(var myProperty: Int = 0)
 * ```
 */
open class InlineSerializer<T, P>(
	private val kSerializer: KSerializer<in P>,
	private val property: (T) -> P,
	private val factory: (P) -> T,
) : KSerializer<T> {
	override val descriptor = kSerializer.descriptor

	@Suppress("UNCHECKED_CAST")
	override fun deserialize(decoder: Decoder): T =
		factory(decoder.decodeSerializableValue(kSerializer as KSerializer<P>))

	override fun serialize(encoder: Encoder, value: T) = encoder.encodeSerializableValue(kSerializer, property(value))
}
