package io.github.ayfri.kore.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible

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
	override val descriptor = kSerializer.descriptor

	@Suppress("UNCHECKED_CAST")
	override fun deserialize(decoder: Decoder): T {
		val value = decoder.decodeSerializableValue(kSerializer as KSerializer<P>)
		val ownerClass = property.parameters[0].type.classifier as KClass<*>
		val constructor =
			ownerClass.primaryConstructor ?: error("No primary constructor found for ${ownerClass.simpleName}")
		constructor.isAccessible = true
		val param = constructor.parameters.first { it.name == property.name }
		return constructor.callBy(mapOf(param to value)) as T
	}

	override fun serialize(encoder: Encoder, value: T) = encoder.encodeSerializableValue(kSerializer, property.get(value))
}
