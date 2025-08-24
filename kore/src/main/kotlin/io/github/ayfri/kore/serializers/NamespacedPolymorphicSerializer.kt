package io.github.ayfri.kore.serializers

import io.github.ayfri.kore.utils.snakeCase
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import kotlinx.serialization.serializer
import net.benwoodworth.knbt.*
import kotlin.reflect.KClass
import kotlin.reflect.full.createType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

open class NamespacedPolymorphicSerializer<T : Any>(
	private val kClass: KClass<T>,
	private val outputName: String = "type",
	private val skipOutputName: Boolean = false,
	private val moveIntoProperty: String? = null,
) : KSerializer<T> {
	override val descriptor = serialDescriptor<JsonElement>()

	override fun deserialize(decoder: Decoder) = error("${kClass.simpleName} cannot be deserialized")

	private fun serializeJson(outputClassName: String, serializer: KSerializer<T>, encoder: JsonEncoder, value: T) {
		val valueJson = encoder.json.encodeToJsonElement(serializer, value)
		if (runCatching { valueJson.jsonObject }.isFailure) {
			encoder.encodeJsonElement(valueJson)
			return
		}

		val finalJson = when (moveIntoProperty) {
			null -> buildJsonObject {
				if (!skipOutputName) put(outputName, "minecraft:$outputClassName")
				valueJson.jsonObject.filterKeys { it != outputName }.forEach(::put)
			}

			else -> buildJsonObject {
				if (!skipOutputName) put(outputName, "minecraft:$outputClassName")

				when (valueJson) {
					is JsonObject -> putJsonObject(moveIntoProperty) {
						valueJson.jsonObject.filterKeys { it != outputName }.forEach(::put)
					}

					else -> put(moveIntoProperty, valueJson)
				}
			}
		}

		encoder.encodeJsonElement(finalJson)
	}

	private fun serializeNbt(outputClassName: String, serializer: KSerializer<T>, encoder: NbtEncoder, value: T) {
		val valueNbt = encoder.nbt.encodeToNbtTag(serializer, value)
		if (runCatching { valueNbt.nbtCompound }.isFailure) {
			encoder.encodeNbtTag(valueNbt)
			return
		}

		val finalJson = when (moveIntoProperty) {
			null -> buildNbtCompound {
				if (!skipOutputName) put(outputName, NbtString("minecraft:$outputClassName"))
				valueNbt.nbtCompound.filterKeys { it != outputName }.forEach(::put)
			}

			else -> buildNbtCompound {
				if (!skipOutputName) put(outputName, NbtString("minecraft:$outputClassName"))

				when (valueNbt) {
					is NbtCompound -> putNbtCompound(moveIntoProperty) {
						valueNbt.nbtCompound.filterKeys { it != outputName }.forEach(::put)
					}

					else -> put(moveIntoProperty, valueNbt)
				}
			}
		}

		encoder.encodeNbtTag(finalJson)
	}

	@OptIn(ExperimentalSerializationApi::class)
	override fun serialize(encoder: Encoder, value: T) {
		require(encoder is JsonEncoder || encoder is NbtEncoder) { "PolymorphicTypeSerializer can only be serialized to Json or Nbt." }
		require(kClass.isInstance(value) && value::class != kClass) { "Value must be instance of ${kClass.simpleName}" }

		val valueClassName = value::class.simpleName!!.snakeCase()
		val outputClassName = when {
			value::class.hasAnnotation<SerialName>() -> value::class.findAnnotation<SerialName>()!!.value
			else -> valueClassName
		}

		val serializer = encoder.serializersModule.getPolymorphic(kClass, value)
			?: encoder.serializersModule.getContextual(value::class)
			?: encoder.serializersModule.serializer(value::class.createType())

		when (encoder) {
			is JsonEncoder -> serializeJson(outputClassName, serializer as KSerializer<T>, encoder, value)
			is NbtEncoder -> serializeNbt(outputClassName, serializer as KSerializer<T>, encoder, value)
		}
	}
}
