package io.github.ayfri.kore.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.benwoodworth.knbt.NbtTag
import kotlin.reflect.KProperty1

/**
 * A serializer that serializes a property of a class.
 *
 * @param T The class that contains the property.
 * @param P The type of the property.
 * @param kSerializer The serializer of the property.
 *
 * Example:
 * ```kotlin
 * data object MyDataClassSerializer : InlineSerializer<MyDataClass, Int>(
 *     kSerializer = Int.serializer(),
 *     property = MyDataClass::myProperty
 * )
 *
 * @Serializable(with = MyDataClassSerializer::class)
 * data class MyDataClass(var myProperty: Int = 0)
 * ```
 */
open class InlineSerializer<T, P>(
	private val kSerializer: KSerializer<in P>,
	private val property: KProperty1<T, P>,
) : KSerializer<T> {
	override val descriptor = NbtTag.serializer().descriptor

	override fun deserialize(decoder: Decoder) = error("InlineSerializer cannot be deserialized")

	override fun serialize(encoder: Encoder, value: T) = encoder.encodeSerializableValue(kSerializer, property.get(value))
}
