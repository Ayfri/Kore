package io.github.ayfri.kore.serializers

import io.github.ayfri.kore.utils.decodeFromNbtTag
import io.github.ayfri.kore.utils.defaultContentName
import io.github.ayfri.kore.utils.encodeToNbtTag
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
 *
 * When [subtypeOf] is set, [outputName] is a property every subtype declares itself. Serializing writes the subtype body
 * unchanged, after checking that [subtypeOf] maps its `minecraft:` [outputName] value to the subtype being written (other
 * namespaces are not checked, since a mod can give them any shape). Decoding reads [outputName], passes its value without
 * the `minecraft:` prefix to [subtypeOf] to get the subtype's content name, and decodes the whole object, [outputName]
 * included. Content names are subtype names (`block_particle_options`), not discriminator values (`block`). For families
 * where several discriminator values share one subtype, like particle options.
 *
 * [inlinedSubtype] is the content name of a subtype that is written without [outputName], as its bare value (a number, or
 * an object for a subtype whose body is itself an object): `constant` for the int, float, height and number providers.
 * Decoding reads it from that bare value or from `{ "type": "<it>", "value": <bare value> }`. An object without
 * [outputName] is decoded as that subtype only, so it fails when it belongs to another one.
 *
 * [decodeUntyped] lets an object without [outputName] be decoded by trying every subtype, for a family whose subtypes
 * are written without it, like the vertical anchors `{ "absolute": 64 }`. Each subtype needs a required field the others
 * lack, otherwise a subtype whose fields all have defaults accepts any object.
 */
open class NamespacedPolymorphicSerializer<T : Any>(
	private val dispatcher: SealedDispatcher<T>,
	private val outputName: String = "type",
	private val skipOutputName: Boolean = false,
	private val moveIntoProperty: String? = null,
	private val useMinecraftPrefix: Boolean = true,
	private val skipEmptyOutput: Boolean = true,
	private val contentName: (String) -> String = ::defaultContentName,
	private val subtypeOf: ((typeName: String) -> String)? = null,
	private val inlinedSubtype: String? = null,
	private val decodeUntyped: Boolean = false,
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
		val name = normalize(typeName).let { subtypeOf?.invoke(it) ?: it }
		val serialName = serialNameByContent.value[name] ?: name
		return dispatcher.serializersBySerialName[serialName] ?: error("No subtype '$typeName' in $baseName")
	}

	/** Decode one already-split `{typeName -> content}` entry, for map-shaped consumers like `ItemStackSubPredicates`. */
	fun deserializeJsonElement(json: Json, typeName: String, element: JsonElement): T =
		json.decodeFromJsonElement(generatedDeserializer(typeName), element)

	/** The fields of a serialized object, without the type name: at the top level or inside [moveIntoProperty]. */
	private fun <E> content(fields: Map<String, E>, asObject: (E) -> Map<String, E>?) = when {
		subtypeOf != null -> fields
		moveIntoProperty == null -> fields - outputName
		else -> fields[moveIntoProperty]?.let(asObject).orEmpty()
	}

	/** The inverse of [content]: [fields] with [typeName] first, moved into [moveIntoProperty] when set. */
	private fun <E> wrap(fields: Map<String, E>, typeName: E, toObject: (Map<String, E>) -> E) =
		if (subtypeOf != null) fields else buildMap {
			if (!skipOutputName) put(outputName, typeName)
			when (moveIntoProperty) {
				null -> putAll(fields - outputName)
				else -> if (!(skipEmptyOutput && fields.isEmpty())) put(moveIntoProperty, toObject(fields - outputName))
			}
		}

	/** A value with no discriminator to read (a bare value, or an object with [decodeUntyped]) is tried against every subtype. */
	private inline fun decodeBare(value: Any, decode: (DeserializationStrategy<T>) -> T): T {
		var failure: Throwable? = null
		return dispatcher.serializersBySerialName.values.firstNotNullOfOrNull {
			runCatching { decode(it) }.onFailure { failure = it }.getOrNull()
		} ?: throw IllegalStateException("No subtype of $baseName can deserialize: $value", failure)
	}

	/** The bare value of an [inlinedSubtype] written as `{ "type": ..., "value": ... }`, `null` for any other subtype. */
	private fun <E> inlinedValue(typeName: String, fields: Map<String, E>) =
		if (inlinedSubtype != null && normalize(typeName) == inlinedSubtype) fields["value"] else null

	override fun deserialize(decoder: Decoder): T = when (decoder) {
		is JsonDecoder -> when (val element = decoder.decodeJsonElement()) {
			is JsonObject -> when (val typeName = element[outputName]?.jsonPrimitive?.content) {
				null -> when {
					inlinedSubtype != null -> decoder.json.decodeFromJsonElement(generatedDeserializer(inlinedSubtype), element)
					decodeUntyped -> decodeBare(element) { decoder.json.decodeFromJsonElement(it, element) }
					else -> error("Missing '$outputName' field in JSON object for $baseName")
				}

				else -> inlinedValue(typeName, element)?.let { decoder.json.decodeFromJsonElement(generatedDeserializer(typeName), it) }
					?: decoder.json.decodeFromJsonElement(generatedDeserializer(typeName), JsonObject(content(element) { it as? JsonObject }))
			}

			else -> decodeBare(element) { decoder.json.decodeFromJsonElement(it, element) }
		}

		else -> when (val tag = (decoder.nbtDecoder ?: error("$baseName can only be deserialized from JSON or NBT.")).decodeNbtTag()) {
			is NbtCompound -> when (val typeName = (tag[outputName] as? NbtString)?.value) {
				null -> when {
					inlinedSubtype != null -> decodeFromNbtTag(generatedDeserializer(inlinedSubtype), tag)
					decodeUntyped -> decodeBare(tag) { decodeFromNbtTag(it, tag) }
					else -> error("Missing '$outputName' field in NBT compound for $baseName")
				}

				else -> inlinedValue(typeName, tag)?.let { decodeFromNbtTag(generatedDeserializer(typeName), it) }
					?: decodeFromNbtTag(generatedDeserializer(typeName), NbtCompound(content(tag) { it as? NbtCompound }))
			}

			else -> decodeBare(tag) { decodeFromNbtTag(it, tag) }
		}
	}

	override fun serialize(encoder: Encoder, value: T) {
		val actual = dispatcher.serializerOf(value)
		val subtype = contentName(actual.descriptor.serialName)

		when (encoder) {
			is JsonEncoder -> encoder.encodeJsonElement(
				when (val body = encoder.json.encodeToJsonElement(actual, value)) {
					is JsonObject -> {
						requireSubtype(body[outputName]?.jsonPrimitive?.content, subtype)
						if (subtype == inlinedSubtype) body else JsonObject(wrap(body, JsonPrimitive(namespaced(subtype)), ::JsonObject))
					}

					else -> body
				}
			)

			else -> (encoder.nbtEncoder ?: error("$baseName can only be serialized to JSON or NBT.")).encodeNbtTag(
				when (val body = encodeToNbtTag(actual, value)) {
					is NbtCompound -> {
						requireSubtype((body[outputName] as? NbtString)?.value, subtype)
						if (subtype == inlinedSubtype) body else NbtCompound(wrap(body, NbtString(namespaced(subtype)), ::NbtCompound))
					}

					else -> body
				}
			)
		}
	}

	/** With [subtypeOf] set, fails when a `minecraft:` [typeName] belongs to another subtype than the one being written. */
	private fun requireSubtype(typeName: String?, subtype: String) {
		if (subtypeOf == null || typeName == null) return
		val id = typeName.removePrefix("minecraft:")
		if (':' in id) return

		val expected = subtypeOf.invoke(id)
		require(expected == subtype) {
			"$typeName doesn't take $subtype options, it takes $expected ones, use the factory matching it."
		}
	}

	/** The Minecraft name (namespaced, per [contentName]) that [value] would serialize under. */
	fun getContentName(value: T) = namespaced(contentName(dispatcher.serializerOf(value).descriptor.serialName))
}
