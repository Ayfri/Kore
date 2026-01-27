package io.github.ayfri.kore.serializers

import io.github.ayfri.kore.utils.getSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.benwoodworth.knbt.NbtTag
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties

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
open class InlineAutoSerializer<T : Any>(val klass: KClass<T>) : KSerializer<T> {
	private val firstProperty =
		klass.declaredMemberProperties.firstOrNull() ?: error("No properties found for class ${klass.simpleName}")

	override val descriptor = NbtTag.serializer().descriptor

	override fun deserialize(decoder: Decoder): T = error("InlineAutoSerializer cannot be deserialized")

	@OptIn(ExperimentalSerializationApi::class)
	override fun serialize(encoder: Encoder, value: T) {
		val propertyValue = firstProperty.get(value) ?: error("First property of ${klass.simpleName} is null")
		val serializer = firstProperty.getSerializer(encoder.serializersModule)

		encoder.encodeSerializableValue(serializer, propertyValue)
	}
}
