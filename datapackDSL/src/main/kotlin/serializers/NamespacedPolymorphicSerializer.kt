package serializers

import kotlin.reflect.KClass
import kotlin.reflect.full.createType
import utils.snakeCase
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
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
		val finalJson = when (moveIntoProperty) {
			null -> buildJsonObject {
				if (!skipOutputName) put(outputName, "minecraft:${value::class.simpleName!!.snakeCase()}")
				valueJson.jsonObject.filterKeys { it != outputName }.forEach(::put)
			}

			else -> buildJsonObject {
				if (!skipOutputName) put(outputName, "minecraft:${value::class.simpleName!!.snakeCase()}")

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
