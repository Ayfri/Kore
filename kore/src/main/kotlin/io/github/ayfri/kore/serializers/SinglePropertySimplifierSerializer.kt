package io.github.ayfri.kore.serializers

import io.github.ayfri.kore.utils.createInstance
import io.github.ayfri.kore.utils.getSerialName
import io.github.ayfri.kore.utils.getSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtDecoder
import net.benwoodworth.knbt.NbtEncoder
import net.benwoodworth.knbt.buildNbtCompound
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

/**
 * A serializer that serializes to a JSON object with only one property if all other properties are null.
 *
 * @param T The class that contains the property.
 * @param P The type of the property.
 * @param kClass The class of the object.
 * @param property The property to serialize.
 *
 * Example:
 * ```kotlin
 * data object MyDataClassSerializer : SinglePropertySimplifierSerializer<MyDataClass, Int>(
 *     kClass = MyDataClass::class,
 *     property = MyDataClass::myProperty
 * )
 *
 * @Serializable(with = MyDataClassSerializer::class)
 * data class MyDataClass(var myProperty: Int = 0, var myOtherProperty: Int? = null)
 *
 * ```
 * If myOtherProperty is null, the JSON will be:
 * ```json
 * 0
 * ```
 * If myOtherProperty is not null, the JSON will be:
 * ```json
 * {
 *    "myProperty": 0,
 *    "myOtherProperty": 0
 * }
 * ```
 */
open class SinglePropertySimplifierSerializer<T : Any, P : Any>(
	private val kClass: KClass<T>,
	private val property: KProperty1<T, P>,
) : KSerializer<T> {
	override val descriptor = buildClassSerialDescriptor("${kClass.simpleName!!}SimplifiableSerializer") {
		element(property.name, serialDescriptor(property.returnType))
	}

	private val properties by lazy {
		val propertiesOrder = kClass.java.declaredFields.withIndex().associate { it.value.name to it.index }
		kClass.memberProperties.associateBy { it.name }.toSortedMap(compareBy { propertiesOrder[it] })
	}

	@Suppress("UNCHECKED_CAST")
	override fun serialize(encoder: Encoder, value: T) {
		require(kClass.isInstance(value) && value::class == kClass) { "Value must be instance of ${kClass.simpleName}" }
		val propertySerializer = property.getSerializer(encoder.serializersModule) as KSerializer<P>

		val propertyValue = properties[property.name]
		val otherProperties = properties.filterKeys { it != property.name }
		if (otherProperties.all {
				it.value.getter.isAccessible = true
				it.value.getter.call(value) == null
			} && propertyValue != null
		) {
			propertyValue.getter.isAccessible = true
			encoder.encodeSerializableValue(propertySerializer, propertyValue.getter.call(value) as P)
		} else
		// the default serializer is the current class, so we can't use it to encode the value, we have to create an object by hand
			when (encoder) {
				is JsonEncoder -> encoder.encodeJsonElement(buildJsonObject {
					properties.forEach { (name, property) ->
						property.getter.isAccessible = true
						val propertyValue = property.getter.call(value)
						if (propertyValue != null) {
							val serialName = property.getSerialName()
							put(
								serialName,
								if (name == this@SinglePropertySimplifierSerializer.property.name)
									encoder.json.encodeToJsonElement(propertySerializer, propertyValue as P)
								else
									encoder.json.encodeToJsonElement(
										property.getSerializer(encoder.serializersModule) as KSerializer<Any>,
										propertyValue
									)
							)
						}
					}
				})

				is NbtEncoder -> encoder.encodeInline(descriptor).encodeSerializableValue(NbtCompound.serializer(), buildNbtCompound {
					properties.forEach { (name, property) ->
						property.getter.isAccessible = true
						val propertyValue = property.getter.call(value)
						if (propertyValue != null) {
							val serialName = property.getSerialName()
							put(
								serialName,
								if (name == this@SinglePropertySimplifierSerializer.property.name)
									encoder.nbt.encodeToNbtTag(propertySerializer, propertyValue as P)
								else
									encoder.nbt.encodeToNbtTag(
										property.getSerializer(encoder.nbt.serializersModule) as KSerializer<Any>,
										propertyValue
									)
							)
						}
					}
				})

				else -> error("Unsupported encoder type: ${encoder::class.simpleName}")
			}
	}

	@Suppress("UNCHECKED_CAST")
	override fun deserialize(decoder: Decoder): T {
		val propertySerializer = property.getSerializer(decoder.serializersModule) as KSerializer<P>

		return when (decoder) {
			is JsonDecoder -> {
				val element = decoder.decodeJsonElement()
				if (element !is JsonObject) {
					val value = decoder.json.decodeFromJsonElement(propertySerializer, element)
					createInstance(mapOf(property.name to value))
				} else {
					val values = properties.mapValues { (_, prop) ->
						val serialName = prop.getSerialName()
						element[serialName]?.let {
							val serializer = prop.getSerializer(decoder.serializersModule)
							decoder.json.decodeFromJsonElement(serializer, it)
						}
					}
					createInstance(values)
				}
			}

			is NbtDecoder -> {
				val tag = decoder.decodeNbtTag()
				if (tag !is NbtCompound) {
					val value = decoder.nbt.decodeFromNbtTag(propertySerializer, tag)
					createInstance(mapOf(property.name to value))
				} else {
					val values = properties.mapValues { (_, prop) ->
						val serialName = prop.getSerialName()
						tag[serialName]?.let {
							val serializer = prop.getSerializer(decoder.serializersModule)
							decoder.nbt.decodeFromNbtTag(serializer, it)
						}
					}
					createInstance(values)
				}
			}

			else -> error("Unsupported decoder type: ${decoder::class.simpleName}")
		}
	}

	@Suppress("UNCHECKED_CAST")
	private fun createInstance(values: Map<String, Any?>): T = kClass.createInstance(values)
}
