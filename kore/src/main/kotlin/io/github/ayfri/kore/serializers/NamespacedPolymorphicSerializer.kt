package io.github.ayfri.kore.serializers

import io.github.ayfri.kore.utils.getSerialName
import io.github.ayfri.kore.utils.snakeCase
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import net.benwoodworth.knbt.*
import kotlin.reflect.KClass
import kotlin.reflect.full.createType
import kotlin.reflect.full.hasAnnotation

open class NamespacedPolymorphicSerializer<T : Any>(
	private val kClass: KClass<T>,
	private val outputName: String = "type",
	private val skipOutputName: Boolean = false,
	private val moveIntoProperty: String? = null,
	private val useMinecraftPrefix: Boolean = true,
	private val skipEmptyOutput: Boolean = true,
) : KSerializer<T> {
	override val descriptor = serialDescriptor<JsonElement>()

	@OptIn(ExperimentalSerializationApi::class)
	override fun deserialize(decoder: Decoder): T {
		require(decoder is JsonDecoder || decoder is NbtDecoder) { "NamespacedPolymorphicSerializer can only be deserialized from Json or Nbt." }

		when (decoder) {
			is JsonDecoder -> {
				val jsonElement = decoder.decodeJsonElement()
				if (jsonElement !is JsonObject) {
					val result = kClass.sealedSubclasses.firstNotNullOfOrNull { subclass ->
						val serializer = decoder.json.serializersModule.getPolymorphic(kClass, subclass.simpleName!!)
							?: runCatching { decoder.json.serializersModule.serializer(subclass.createType()) }.getOrNull()

						@Suppress("UNCHECKED_CAST")
						serializer?.let {
							runCatching {
								decoder.json.decodeFromJsonElement(
									it as DeserializationStrategy<T>,
									jsonElement
								)
							}.getOrNull()
						}
					}
					return result
						?: error("No subclass of ${kClass.simpleName} can deserialize non-object JSON element: $jsonElement")
				}
				val jsonObject = jsonElement.jsonObject
				val typeName = jsonObject[outputName]?.jsonPrimitive?.content
					?: error("Missing '$outputName' field in JSON object for ${kClass.simpleName}")

				val subclass = findSubclass(typeName)
				val serializer = decoder.json.serializersModule.getPolymorphic(kClass, typeName)
					?: decoder.json.serializersModule.serializer(subclass.createType()) as DeserializationStrategy<T>

				val contentJson = when (moveIntoProperty) {
					null -> buildJsonObject {
						jsonObject.filterKeys { it != outputName }.forEach(::put)
					}

					else -> jsonObject[moveIntoProperty]?.jsonObject ?: buildJsonObject {}
				}

				@Suppress("UNCHECKED_CAST")
				return decoder.json.decodeFromJsonElement(serializer, contentJson) as T
			}

			is NbtDecoder -> {
				val nbtTag = decoder.decodeNbtTag()
				if (nbtTag !is NbtCompound) {
					val result = kClass.sealedSubclasses.firstNotNullOfOrNull { subclass ->
						val serializer = decoder.nbt.serializersModule.getPolymorphic(kClass, subclass.simpleName!!)
							?: runCatching { decoder.nbt.serializersModule.serializer(subclass.createType()) }.getOrNull()

						@Suppress("UNCHECKED_CAST")
						serializer?.let {
							runCatching {
								decoder.nbt.decodeFromNbtTag(
									it as DeserializationStrategy<T>,
									nbtTag
								)
							}.getOrNull()
						}
					}
					return result
						?: error("No subclass of ${kClass.simpleName} can deserialize non-compound NBT element: $nbtTag")
				}
				val nbtCompound = nbtTag.nbtCompound
				val typeName = nbtCompound[outputName]?.let { (it as NbtString).value }
					?: error("Missing '$outputName' field in NBT compound for ${kClass.simpleName}")

				val subclass = findSubclass(typeName)
				val serializer = decoder.nbt.serializersModule.getPolymorphic(kClass, typeName)
					?: decoder.nbt.serializersModule.serializer(subclass.createType()) as DeserializationStrategy<T>

				val contentNbt = when (moveIntoProperty) {
					null -> buildNbtCompound {
						nbtCompound.filterKeys { it != outputName }.forEach(::put)
					}

					else -> nbtCompound[moveIntoProperty]?.nbtCompound ?: buildNbtCompound {}
				}

				return decoder.nbt.decodeFromNbtTag(serializer, contentNbt)
			}

			else -> error("Unsupported decoder type")
		}
	}

	private fun findSubclass(typeName: String): KClass<out T> {
		val name =
			if (useMinecraftPrefix && typeName.startsWith("minecraft:")) typeName.removePrefix("minecraft:") else typeName

		return kClass.sealedSubclasses.firstOrNull { subclass ->
			val serialName = subclass.getSerialName().let {
				if (subclass.hasAnnotation<SerialName>()) it else it.snakeCase()
			}
			serialName == name
		} ?: error("No subclass of ${kClass.simpleName} found for type '$typeName'")
	}

	private fun serializeJson(outputClassName: String, serializer: SerializationStrategy<T>, encoder: JsonEncoder, value: T) {
		val valueJson = encoder.json.encodeToJsonElement(serializer, value)
		if (runCatching { valueJson.jsonObject }.isFailure) {
			encoder.encodeJsonElement(valueJson)
			return
		}

		val finalJson = when (moveIntoProperty) {
			null -> buildJsonObject {
				if (!skipOutputName) put(outputName, outputClassName)
				valueJson.jsonObject.filterKeys { it != outputName }.forEach(::put)
			}

			else -> buildJsonObject {
				if (!skipOutputName) put(outputName, outputClassName)

				when (valueJson) {
					is JsonObject if skipEmptyOutput && valueJson.isEmpty() -> Unit
					is JsonObject -> putJsonObject(moveIntoProperty) {
						valueJson.jsonObject.filterKeys { it != outputName }.forEach(::put)
					}

					else -> put(moveIntoProperty, valueJson)
				}
			}
		}

		encoder.encodeJsonElement(finalJson)
	}

	private fun serializeNbt(outputClassName: String, serializer: SerializationStrategy<T>, encoder: NbtEncoder, value: T) {
		val valueNbt = encoder.nbt.encodeToNbtTag(serializer, value)
		if (runCatching { valueNbt.nbtCompound }.isFailure) {
			encoder.encodeNbtTag(valueNbt)
			return
		}

		val finalJson = when (moveIntoProperty) {
			null -> buildNbtCompound {
				if (!skipOutputName) put(outputName, NbtString(outputClassName))
				valueNbt.nbtCompound.filterKeys { it != outputName }.forEach(::put)
			}

			else -> buildNbtCompound {
				if (!skipOutputName) put(outputName, NbtString(outputClassName))

				when (valueNbt) {
					is NbtCompound if skipEmptyOutput && valueNbt.isEmpty() -> Unit
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

		val namespacedOutputClassName = getContentName(value)

		val serializer = encoder.serializersModule.getPolymorphic(kClass, value)
			?: encoder.serializersModule.serializer(value::class.createType()) as SerializationStrategy<T>

		when (encoder) {
			is JsonEncoder -> serializeJson(namespacedOutputClassName, serializer, encoder, value)
			is NbtEncoder -> serializeNbt(namespacedOutputClassName, serializer, encoder, value)
		}
	}

	fun getContentName(value: T): String {
		val outputClassName = value::class.getSerialName().let {
			if (value::class.hasAnnotation<SerialName>()) it else it.snakeCase()
		}
		return if (useMinecraftPrefix) "minecraft:$outputClassName" else outputClassName
	}
}
