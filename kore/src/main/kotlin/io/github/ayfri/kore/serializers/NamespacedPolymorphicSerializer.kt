package io.github.ayfri.kore.serializers

import io.github.ayfri.kore.utils.snakeCase
import kotlin.reflect.KClass
import kotlin.reflect.full.createType
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import kotlinx.serialization.serializer

open class NamespacedPolymorphicSerializer<T : Any>(
	private val kClass: KClass<T>,
	private val outputName: String = "type",
	private val skipOutputName: Boolean = false,
	private val moveIntoProperty: String? = null,
) : KSerializer<T> {
	override val descriptor = serialDescriptor<JsonElement>()

	override fun deserialize(decoder: Decoder) = error("${kClass.simpleName} cannot be deserialized")

	@OptIn(ExperimentalSerializationApi::class)
	override fun serialize(encoder: Encoder, value: T) {
		require(encoder is JsonEncoder) { "PolymorphicTypeSerializer can only be serialized to Json" }
		require(kClass.isInstance(value) && value::class != kClass) { "Value must be instance of ${kClass.simpleName}" }

		val serializer = encoder.serializersModule.getPolymorphic(kClass, value)
			?: encoder.serializersModule.getContextual(value::class)
			?: encoder.serializersModule.serializer(value::class.createType())

		val valueJson = encoder.json.encodeToJsonElement(serializer as KSerializer<T>, value)
		val valueClassName = value::class.simpleName!!.snakeCase()
		val outputClassName = when {
			value::class.annotations.any { it is SerialName } -> value::class.annotations.filterIsInstance<SerialName>().first().value
			else -> valueClassName
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
}
