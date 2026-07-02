package io.github.ayfri.kore.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * A serializer that serializes a single-property wrapper class as its property value directly.
 *
 * @param T The target class type.
 * @param P The type of the wrapped property.
 * @param propertySerializer The serializer for the wrapped property.
 * @param property Getter for the wrapped property.
 * @param factory Constructor reference building [T] from the property value.
 *
 * Example:
 * ```kotlin
 * @Serializable(with = MyDataClass.Companion.MyDataClassSerializer::class)
 * data class MyDataClass(var myProperty: Int = 0) {
 *     companion object {
 *         data object MyDataClassSerializer :
 *             InlineAutoSerializer<MyDataClass, Int>(serializer<Int>(), MyDataClass::myProperty, ::MyDataClass)
 *     }
 * }
 * ```
 */
open class InlineAutoSerializer<T, P>(
	private val propertySerializer: KSerializer<P>,
	private val property: (T) -> P,
	private val factory: (P) -> T,
) : KSerializer<T> {
	override val descriptor = propertySerializer.descriptor

	override fun serialize(encoder: Encoder, value: T) =
		encoder.encodeSerializableValue(propertySerializer, property(value))

	override fun deserialize(decoder: Decoder): T =
		factory(decoder.decodeSerializableValue(propertySerializer))
}
