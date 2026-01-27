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
 * A serializer that inlines either of 2 properties where they are both nullable but at least one is required.
 *
 * @param T The target class type.
 * @param kClass The class of the object.
 * @param property1 The first property to check.
 * @param property2 The second property to check.
 * @param inline Whether to inline the property value or wrap it in an object with the property name, `true` by default.
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
	private val property1: KProperty1<T, *>,
	private val property2: KProperty1<T, *>,
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

		property1.getter.isAccessible = true
		val v1 = property1.getter.call(value)
		property2.getter.isAccessible = true
		val v2 = property2.getter.call(value)

		require(v1 != null || v2 != null) { "At least one of ${property1.name} or ${property2.name} must be non-null in ${kClass.simpleName}" }

		if (inline) {
			val (property, propertyValue) = if (v1 != null) property1 to v1 else property2 to v2!!
			val serializer = property.getSerializer(encoder.serializersModule) as KSerializer<Any>

			encoder.encodeSerializableValue(serializer, propertyValue)
		} else {
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
	}

	@Suppress("UNCHECKED_CAST")
	override fun deserialize(decoder: Decoder): T {
		if (inline) {
			val property1Serializer = property1.getSerializer(decoder.serializersModule) as KSerializer<Any>
			val property2Serializer = property2.getSerializer(decoder.serializersModule) as KSerializer<Any>

			return when (decoder) {
				is JsonDecoder -> {
					val element = decoder.decodeJsonElement()
					val v1 = runCatching {
						decoder.json.decodeFromJsonElement(property1Serializer, element)
					}.getOrNull()

					if (v1 != null) {
						kClass.createInstance(mapOf(property1.name to v1))
					} else {
						val v2 = runCatching {
							decoder.json.decodeFromJsonElement(property2Serializer, element)
						}.getOrNull()
						if (v2 != null) {
							kClass.createInstance(mapOf(property2.name to v2))
						} else {
							error("Could not deserialize either ${property1.name} or ${property2.name} from $element")
						}
					}
				}

				is NbtDecoder -> {
					val tag = decoder.decodeNbtTag()
					val v1 = runCatching {
						decoder.nbt.decodeFromNbtTag(property1Serializer, tag)
					}.getOrNull()

					if (v1 != null) {
						kClass.createInstance(mapOf(property1.name to v1))
					} else {
						val v2 = runCatching {
							decoder.nbt.decodeFromNbtTag(property2Serializer, tag)
						}.getOrNull()
						if (v2 != null) {
							kClass.createInstance(mapOf(property2.name to v2))
						} else {
							error("Could not deserialize either ${property1.name} or ${property2.name} from $tag")
						}
					}
				}

				else -> error("Unsupported decoder type: ${decoder::class.simpleName}")
			}
		} else {
			val values = when (decoder) {
				is JsonDecoder -> {
					val element = decoder.decodeJsonElement() as? JsonObject ?: error("Expected JsonObject")
					properties.mapValues { (_, prop) ->
						val serialName = prop.getSerialName()
						element[serialName]?.let {
							decoder.json.decodeFromJsonElement(
								prop.getSerializer(decoder.serializersModule) as KSerializer<Any>,
								it
							)
						}
					}
				}

				is NbtDecoder -> {
					val tag = decoder.decodeNbtTag() as? NbtCompound ?: error("Expected NbtCompound")
					properties.mapValues { (_, prop) ->
						val serialName = prop.getSerialName()
						tag[serialName]?.let {
							decoder.nbt.decodeFromNbtTag(
								prop.getSerializer(decoder.serializersModule) as KSerializer<Any>,
								it
							)
						}
					}
				}

				else -> error("Unsupported decoder type: ${decoder::class.simpleName}")
			}

			require(values[property1.name] != null || values[property2.name] != null) {
				"At least one of ${property1.getSerialName()} or ${property2.getSerialName()} must be present"
			}

			return kClass.createInstance(values)
		}
	}
}
