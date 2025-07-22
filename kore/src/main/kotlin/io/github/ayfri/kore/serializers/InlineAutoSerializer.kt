package io.github.ayfri.kore.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor
import net.benwoodworth.knbt.NbtTag

/**
 * A serializer that serializes the first property of a class automatically.
 *
 * @param T The target class type.
 *
 * Example:
 * ```kotlin
 * data object MyDataClassSerializer : InlineAutoSerializer<MyDataClass>()
 *
 * @Serializable(with = MyDataClassSerializer::class)
 * data class MyDataClass(var myProperty: Int = 0)
 * ```
 */
open class InlineAutoSerializer<T : Any>(klass: KClass<T>) : KSerializer<T> {
	private val firstProperty: KProperty1<T, *> =
		klass.declaredMemberProperties.firstOrNull()
			?: error("No properties found for class ${klass.simpleName}")
	private val propertySerializer: KSerializer<Any?> = @Suppress("UNCHECKED_CAST")
	(serializer(firstProperty.returnType) as KSerializer<Any?>)

	override val descriptor = NbtTag.serializer().descriptor

	override fun deserialize(decoder: Decoder): T = error("InlineAutoSerializer cannot be deserialized")

	override fun serialize(encoder: Encoder, value: T) {
		encoder.encodeSerializableValue(propertySerializer, firstProperty.get(value))
	}
}
