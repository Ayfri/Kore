package io.github.ayfri.kore.serializers

import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtEncoder
import net.benwoodworth.knbt.buildNbtCompound
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.serializer

open class SinglePropertySimplifierSerializer<T : Any, P : Any>(
	private val kClass: KClass<T>,
	private val property: KProperty1<T, P>,
) : KSerializer<T> {
	override val descriptor = buildClassSerialDescriptor("${kClass.simpleName!!}SimplifiableSerializer") {
		element(property.name, serialDescriptor(property.returnType))
	}

	override fun serialize(encoder: Encoder, value: T) {
		require(kClass.isInstance(value) && value::class == kClass) { "Value must be instance of ${kClass.simpleName}" }
		var propertySerializer = encoder.serializersModule.serializer(property.returnType) as KSerializer<P>

		property.annotations.filterIsInstance<Serializable>().firstOrNull()?.let {
			propertySerializer = (it.with.objectInstance ?: it.with.createInstance()) as KSerializer<P>
		}

		val propertiesOrder = kClass.java.declaredFields.withIndex().associate { it.value.name to it.index }
		val properties = kClass.memberProperties.associateBy { it.name }.toSortedMap(compareBy { propertiesOrder[it] })
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
		// default serializer is the current class, so we can't use it to encode the value, we have to create an object by hand
			when (encoder) {
				is JsonEncoder -> encoder.encodeJsonElement(buildJsonObject {
					properties.forEach { (name, property) ->
						property.getter.isAccessible = true
						val propertyValue = property.getter.call(value)
						if (propertyValue != null) {
							val serialName = property.annotations.filterIsInstance<SerialName>().firstOrNull()?.value ?: name
							put(
								serialName,
								if (name == this@SinglePropertySimplifierSerializer.property.name)
									encoder.json.encodeToJsonElement(propertySerializer, propertyValue as P)
								else
									encoder.json.encodeToJsonElement(
										(property.annotations.filterIsInstance<Serializable>().firstOrNull()?.let {
											(it.with.objectInstance ?: it.with.createInstance())
										} ?: encoder.serializersModule.serializer(property.returnType)) as KSerializer<Any>,
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
							val serialName = property.annotations.filterIsInstance<SerialName>().firstOrNull()?.value ?: name
							put(
								serialName,
								if (name == this@SinglePropertySimplifierSerializer.property.name)
									encoder.nbt.encodeToNbtTag(propertySerializer, propertyValue as P)
								else
									encoder.nbt.encodeToNbtTag(
										(property.annotations.filterIsInstance<Serializable>().firstOrNull()?.let {
											(it.with.objectInstance ?: it.with.createInstance())
										} ?: encoder.serializersModule.serializer(property.returnType)) as KSerializer<Any>,
										propertyValue
									)
							)
						}
					}
				})

				else -> error("Unsupported encoder type: ${encoder::class.simpleName}")
			}
	}

	override fun deserialize(decoder: Decoder) =
		error("${kClass.simpleName} is not deserializable with ${this::class.simpleName}.")
}
