package io.github.ayfri.kore.serializers

import io.github.ayfri.kore.utils.defaultContentName
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import net.benwoodworth.knbt.*

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

	/** The fields of a serialized object, without the type name: at the top level or inside [moveIntoProperty]. */
	private fun <E> content(fields: Map<String, E>, asObject: (E) -> Map<String, E>?) = when (moveIntoProperty) {
		null -> fields - outputName
		else -> fields[moveIntoProperty]?.let(asObject).orEmpty()
	}

	/** The inverse of [content]: [fields] with [typeName] first, moved into [moveIntoProperty] when set. */
	private fun <E> wrap(fields: Map<String, E>, typeName: E, toObject: (Map<String, E>) -> E) = buildMap {
		if (!skipOutputName) put(outputName, typeName)
		when (moveIntoProperty) {
			null -> putAll(fields - outputName)
			else -> if (!(skipEmptyOutput && fields.isEmpty())) put(moveIntoProperty, toObject(fields - outputName))
		}
	}

	/** A bare (non-object) value has no discriminator to read, so every subtype is tried until one decodes it. */
	private inline fun decodeBare(value: Any, decode: (DeserializationStrategy<T>) -> T) =
		dispatcher.serializersBySerialName.values.firstNotNullOfOrNull { runCatching { decode(it) }.getOrNull() }
			?: error("No subtype of $baseName can deserialize non-object value: $value")

	override fun deserialize(decoder: Decoder): T = when (decoder) {
		is JsonDecoder -> when (val element = decoder.decodeJsonElement()) {
			is JsonObject -> {
				val typeName = element[outputName]?.jsonPrimitive?.content
					?: error("Missing '$outputName' field in JSON object for $baseName")
				decoder.json.decodeFromJsonElement(generatedDeserializer(typeName), JsonObject(content(element) { it as? JsonObject }))
			}

			else -> decodeBare(element) { decoder.json.decodeFromJsonElement(it, element) }
		}

		is NbtDecoder -> when (val tag = decoder.decodeNbtTag()) {
			is NbtCompound -> {
				val typeName = (tag[outputName] as? NbtString)?.value
					?: error("Missing '$outputName' field in NBT compound for $baseName")
				decoder.nbt.decodeFromNbtTag(generatedDeserializer(typeName), NbtCompound(content(tag) { it as? NbtCompound }))
			}

			else -> decodeBare(tag) { decoder.nbt.decodeFromNbtTag(it, tag) }
		}

		else -> error("$baseName can only be deserialized from JSON or NBT.")
	}

	override fun serialize(encoder: Encoder, value: T) {
		val actual = dispatcher.serializerOf(value)
		val outputClassName = namespaced(contentName(actual.descriptor.serialName))

		when (encoder) {
			is JsonEncoder -> encoder.encodeJsonElement(
				when (val body = encoder.json.encodeToJsonElement(actual, value)) {
					is JsonObject -> JsonObject(wrap(body, JsonPrimitive(outputClassName), ::JsonObject))
					else -> body
				}
			)

			is NbtEncoder -> encoder.encodeNbtTag(
				when (val body = encoder.nbt.encodeToNbtTag(actual, value)) {
					is NbtCompound -> NbtCompound(wrap(body, NbtString(outputClassName), ::NbtCompound))
					else -> body
				}
			)

			else -> error("$baseName can only be serialized to JSON or NBT.")
		}
	}

	/** The Minecraft name (namespaced, per [contentName]) that [value] would serialize under. */
	fun getContentName(value: T) = namespaced(contentName(dispatcher.serializerOf(value).descriptor.serialName))
}
