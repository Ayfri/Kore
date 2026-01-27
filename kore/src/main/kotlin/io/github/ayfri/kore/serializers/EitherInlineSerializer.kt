package io.github.ayfri.kore.serializers

import io.github.ayfri.kore.utils.createInstance
import io.github.ayfri.kore.utils.getSerialName
import io.github.ayfri.kore.utils.getSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.*
import net.benwoodworth.knbt.*
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

/**
 * A serializer that inlines one of the specified properties if it's non-null.
 * If none of the specified properties are present (or if [inline] is false), it serializes as an object.
 *
 * @param T The target class type.
 * @param kClass The class of the object.
 * @param propertiesToInline The properties to check for inlining.
 * @param inline Whether to attempt to inline the first non-null property from [propertiesToInline].
 *
 * Example:
 * ```kotlin
 * data object MyClassSerializer : EitherInlineSerializer<MyClass>(
 *     MyClass::class,
 *     MyClass::p1,
 *     MyClass::p2
 * )
 *
 * @Serializable(with = MyClassSerializer::class)
 * data class MyClass(val p1: String? = null, val p2: Int? = null)
 * ```
 */
open class EitherInlineSerializer<T : Any>(
	private val kClass: KClass<T>,
	private vararg val propertiesToInline: KProperty1<T, *>,
	private val inline: Boolean = true,
) : KSerializer<T> {
	private val properties by lazy {
		val propertiesOrder = kClass.java.declaredFields.withIndex().associate { it.value.name to it.index }
		kClass.memberProperties.associateBy { it.name }.toSortedMap(compareBy { propertiesOrder[it] })
	}

	override val descriptor = buildClassSerialDescriptor("${kClass.simpleName!!}EitherInlineSerializer") {
		properties.forEach { (_, prop) ->
			element(prop.getSerialName(), serialDescriptor(prop.returnType), isOptional = true)
		}
	}

	@Suppress("UNCHECKED_CAST")
	override fun serialize(encoder: Encoder, value: T) {
		require(kClass.isInstance(value)) { "Value must be instance of ${kClass.simpleName}" }

		if (inline) {
			for (prop in propertiesToInline) {
				prop.getter.isAccessible = true
				val propValue = prop.getter.call(value)
				if (propValue != null) {
					val serializer = prop.getSerializer(encoder.serializersModule) as KSerializer<Any>
					encoder.encodeSerializableValue(serializer, propValue)
					return
				}
			}
		}

		when (encoder) {
			is JsonEncoder -> encoder.encodeJsonElement(buildJsonObject {
				properties.forEach { (_, property) ->
					property.getter.isAccessible = true
					val propertyValue = property.getter.call(value)
					if (propertyValue != null) {
						val serialName = property.getSerialName()
						val serializer = property.getSerializer(encoder.serializersModule) as KSerializer<Any>
						put(serialName, encoder.json.encodeToJsonElement(serializer, propertyValue))
					}
				}
			})

			is NbtEncoder -> encoder.encodeInline(descriptor).encodeSerializableValue(
				NbtCompound.serializer(),
				buildNbtCompound {
					properties.forEach { (_, property) ->
						property.getter.isAccessible = true
						val propertyValue = property.getter.call(value)
						if (propertyValue != null) {
							val serialName = property.getSerialName()
							val serializer = property.getSerializer(encoder.serializersModule) as KSerializer<Any>
							put(serialName, encoder.nbt.encodeToNbtTag(serializer, propertyValue))
						}
					}
				})

			else -> encoder.encodeStructure(descriptor) {
				properties.values.forEachIndexed { index, property ->
					property.getter.isAccessible = true
					val propertyValue = property.getter.call(value)
					if (propertyValue != null) {
						val serializer = property.getSerializer(encoder.serializersModule) as KSerializer<Any>
						encodeSerializableElement(descriptor, index, serializer, propertyValue)
					}
				}
			}
		}
	}

	@Suppress("UNCHECKED_CAST")
	override fun deserialize(decoder: Decoder): T {
		val element: Any = when (decoder) {
			is JsonDecoder -> decoder.decodeJsonElement()
			is NbtDecoder -> decoder.decodeNbtTag()
			else -> error("Unsupported decoder type: ${decoder::class.simpleName}")
		}

		if (inline) {
			for (prop in propertiesToInline) {
				val serializer = prop.getSerializer(decoder.serializersModule) as KSerializer<Any>
				val value = runCatching {
					when (decoder) {
						is JsonDecoder -> decoder.json.decodeFromJsonElement(serializer, element as JsonElement)
						is NbtDecoder -> decoder.nbt.decodeFromNbtTag(serializer, element as NbtTag)
						else -> null
					}
				}.getOrNull()

				if (value != null) {
					return kClass.createInstance(mapOf(prop.name to value))
				}
			}
		}

		val values = when (decoder) {
			is JsonDecoder -> {
				val jsonObject = element as? JsonObject ?: error("Expected JsonObject for $kClass but got $element")
				properties.mapValues { (_, prop) ->
					val serialName = prop.getSerialName()
					jsonObject[serialName]?.let {
						decoder.json.decodeFromJsonElement(
							prop.getSerializer(decoder.serializersModule) as KSerializer<Any>,
							it
						)
					}
				}
			}

			is NbtDecoder -> {
				val nbtCompound = element as? NbtCompound ?: error("Expected NbtCompound for $kClass but got $element")
				properties.mapValues { (_, prop) ->
					val serialName = prop.getSerialName()
					nbtCompound[serialName]?.let {
						decoder.nbt.decodeFromNbtTag(
							prop.getSerializer(decoder.serializersModule) as KSerializer<Any>,
							it
						)
					}
				}
			}

			else -> error("Unsupported decoder type: ${decoder::class.simpleName}")
		}

		return kClass.createInstance(values)
	}
}
