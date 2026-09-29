package io.github.ayfri.kore.serializers

import io.github.ayfri.kore.utils.copyAllFrom
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.snakeCase
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import net.benwoodworth.knbt.*

internal fun defaultContentName(serialName: String) = serialName.substringAfterLast('.').snakeCase()

/**
 * Serializes a sealed hierarchy as Minecraft's `{ "type": "<namespace:name>", ...fields }` shape, resolving every case
 * automatically, reflection-free - no case list to maintain. Non-object cases (a subtype that serializes to a bare
 * array or primitive) are emitted as-is, which is why this exists instead of kotlinx's built-in polymorphism.
 *
 * Pass the KSP-generated [SealedDispatcher] (see [GeneratedSealedSerializer]):
 * ```kotlin
 * @GeneratedSealedSerializer
 * @Serializable(with = Foo.Companion.FooSerializer::class)
 * sealed class Foo {
 *     companion object {
 *         data object FooSerializer : NamespacedPolymorphicSerializer<Foo>(fooSealedSerializer())
 *     }
 * }
 * ```
 */
open class NamespacedPolymorphicSerializer<T : Any>(
	private val dispatcher: SealedDispatcher<T>,
	private val outputName: String = "type",
	private val skipOutputName: Boolean = false,
	private val moveIntoProperty: String? = null,
	private val useMinecraftPrefix: Boolean = true,
	private val skipEmptyOutput: Boolean = true,
	private val contentName: (String) -> String = ::defaultContentName,
) : KSerializer<T> {
	override val descriptor = serialDescriptor<JsonElement>()

	private val baseName get() = dispatcher.serialName
	private fun namespaced(name: String) = if (useMinecraftPrefix) "minecraft:$name" else name
	private fun normalize(typeName: String) =
		if (useMinecraftPrefix) typeName.removePrefix("minecraft:") else typeName

	private val serialNameByContent = lazy { dispatcher.serializersBySerialName.keys.associateBy(contentName) }

	/** Every subtype's Minecraft name, e.g. `["enchantments", "damage", ...]`. */
	val contentNames get() = dispatcher.serializersBySerialName.keys.map(contentName)

	private fun generatedDeserializer(typeName: String): DeserializationStrategy<T> {
		val serialName = serialNameByContent.value[normalize(typeName)] ?: normalize(typeName)
		return dispatcher.serializersBySerialName[serialName] ?: error("No subtype '$typeName' in $baseName")
	}

	/** Decode one already-split `{typeName -> content}` entry, for map-shaped consumers like `ItemStackSubPredicates`. */
	fun deserializeJsonElement(json: Json, typeName: String, element: JsonElement): T =
		json.decodeFromJsonElement(generatedDeserializer(typeName), element)

	private fun contentJson(jsonObject: JsonObject) = when (moveIntoProperty) {
		null -> buildJsonObject { copyAllFrom(jsonObject, outputName) }
		else -> jsonObject[moveIntoProperty]?.jsonObject ?: buildJsonObject {}
	}

	private fun contentNbt(nbtCompound: NbtCompound) = when (moveIntoProperty) {
		null -> nbt { nbtCompound.filterKeys { it != outputName }.forEach { (key, tag) -> put(key, tag) } }
		else -> nbtCompound[moveIntoProperty]?.nbtCompound ?: nbt {}
	}

	@OptIn(ExperimentalSerializationApi::class)
	override fun deserialize(decoder: Decoder): T {
		require(decoder is JsonDecoder || decoder is NbtDecoder) { "NamespacedPolymorphicSerializer can only be deserialized from Json or Nbt." }

		return when (decoder) {
			is JsonDecoder -> when (val element = decoder.decodeJsonElement()) {
				is JsonObject -> {
					val typeName = element[outputName]?.jsonPrimitive?.content
						?: error("Missing '$outputName' field in JSON object for $baseName")
					decoder.json.decodeFromJsonElement(generatedDeserializer(typeName), contentJson(element))
				}

				else -> deserializeBareJson(decoder, element)
			}

			is NbtDecoder -> when (val tag = decoder.decodeNbtTag()) {
				is NbtCompound -> {
					val typeName = tag[outputName]?.let { (it as NbtString).value }
						?: error("Missing '$outputName' field in NBT compound for $baseName")
					decoder.nbt.decodeFromNbtTag(generatedDeserializer(typeName), contentNbt(tag))
				}

				else -> deserializeBareNbt(decoder, tag)
			}

			else -> error("Unsupported decoder type")
		}
	}

	// A bare (non-object) element has no discriminator to read, so try every subtype until one decodes it.
	private fun deserializeBareJson(decoder: JsonDecoder, element: JsonElement): T =
		dispatcher.serializersBySerialName.values.firstNotNullOfOrNull { serializer ->
			runCatching { decoder.json.decodeFromJsonElement(serializer, element) }.getOrNull()
		} ?: error("No subtype of $baseName can deserialize non-object JSON element: $element")

	private fun deserializeBareNbt(decoder: NbtDecoder, tag: NbtTag): T =
		dispatcher.serializersBySerialName.values.firstNotNullOfOrNull { serializer ->
			runCatching { decoder.nbt.decodeFromNbtTag(serializer, tag) }.getOrNull()
		} ?: error("No subtype of $baseName can deserialize non-compound NBT element: $tag")

	private fun serializeJson(outputClassName: String, valueJson: JsonElement, encoder: JsonEncoder) {
		if (valueJson !is JsonObject) {
			encoder.encodeJsonElement(valueJson)
			return
		}

		val finalJson = when (moveIntoProperty) {
			null -> buildJsonObject {
				if (!skipOutputName) put(outputName, outputClassName)
				copyAllFrom(valueJson, outputName)
			}

			else -> buildJsonObject {
				if (!skipOutputName) put(outputName, outputClassName)
				if (!(skipEmptyOutput && valueJson.isEmpty()))
					putJsonObject(moveIntoProperty) { copyAllFrom(valueJson, outputName) }
			}
		}

		encoder.encodeJsonElement(finalJson)
	}

	private fun serializeNbt(outputClassName: String, valueNbt: NbtTag, encoder: NbtEncoder) {
		if (valueNbt !is NbtCompound) {
			encoder.encodeNbtTag(valueNbt)
			return
		}

		val finalNbt = when (moveIntoProperty) {
			null -> nbt {
				if (!skipOutputName) put(outputName, NbtString(outputClassName))
				valueNbt.filterKeys { it != outputName }.forEach { (key, tag) -> put(key, tag) }
			}

			else -> nbt {
				if (!skipOutputName) put(outputName, NbtString(outputClassName))
				if (!(skipEmptyOutput && valueNbt.isEmpty()))
					putNbtCompound(moveIntoProperty) { valueNbt.filterKeys { it != outputName }.forEach { (key, tag) -> put(key, tag) } }
			}
		}

		encoder.encodeNbtTag(finalNbt)
	}

	override fun serialize(encoder: Encoder, value: T) {
		require(encoder is JsonEncoder || encoder is NbtEncoder) { "PolymorphicTypeSerializer can only be serialized to Json or Nbt." }

		val actual = dispatcher.serializerOf(value)
		val outputClassName = namespaced(contentName(actual.descriptor.serialName))

		when (encoder) {
			is JsonEncoder -> serializeJson(outputClassName, encoder.json.encodeToJsonElement(actual, value), encoder)
			is NbtEncoder -> serializeNbt(outputClassName, encoder.nbt.encodeToNbtTag(actual, value), encoder)
		}
	}

	/** The Minecraft name (namespaced, per [contentName]) that [value] would serialize under. */
	fun getContentName(value: T) = namespaced(contentName(dispatcher.serializerOf(value).descriptor.serialName))
}
