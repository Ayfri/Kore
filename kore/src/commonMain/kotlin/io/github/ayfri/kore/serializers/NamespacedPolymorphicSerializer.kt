package io.github.ayfri.kore.serializers

import io.github.ayfri.kore.utils.copyAllFrom
import io.github.ayfri.kore.utils.nbt
import io.github.ayfri.kore.utils.snakeCase
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.internal.AbstractPolymorphicSerializer
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import net.benwoodworth.knbt.*

internal fun defaultContentName(serialName: String) = serialName.substringAfterLast('.').snakeCase()

/**
 * Serializes a sealed hierarchy as Minecraft's `{ "type": "<namespace:name>", ...fields }` shape, resolving every case
 * automatically, reflection-free - no case list to maintain. Non-object cases (a subtype that serializes to a bare
 * array or primitive) are emitted as-is, which is why this exists instead of kotlinx's built-in polymorphism.
 *
 * Pass the KSP-generated sealed serializer (see [GeneratedSealedSerializer]):
 * ```kotlin
 * @GeneratedSealedSerializer
 * @Serializable(with = Foo.Companion.FooSerializer::class)
 * sealed class Foo {
 *     companion object {
 *         @OptIn(InternalSerializationApi::class)
 *         data object FooSerializer : NamespacedPolymorphicSerializer<Foo>(fooSealedSerializer())
 *     }
 * }
 * ```
 */
@OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
open class NamespacedPolymorphicSerializer<T : Any>(
	generated: KSerializer<T>,
	private val outputName: String = "type",
	private val skipOutputName: Boolean = false,
	private val moveIntoProperty: String? = null,
	private val useMinecraftPrefix: Boolean = true,
	private val skipEmptyOutput: Boolean = true,
	private val contentName: (String) -> String = ::defaultContentName,
) : KSerializer<T> {
	@OptIn(InternalSerializationApi::class)
	private val polymorphic = generated as AbstractPolymorphicSerializer<T>

	override val descriptor = serialDescriptor<JsonElement>()

	private val baseName get() = polymorphic.descriptor.serialName
	private fun namespaced(name: String) = if (useMinecraftPrefix) "minecraft:$name" else name
	private fun normalize(typeName: String) =
		if (useMinecraftPrefix) typeName.removePrefix("minecraft:") else typeName

	@OptIn(ExperimentalSerializationApi::class)
	private val serialNames: List<String> by lazy {
		val descriptor = polymorphic.descriptor
		if (descriptor.kind != PolymorphicKind.SEALED) return@lazy emptyList()
		descriptor.getElementDescriptor(1)
			.let { variants -> List(variants.elementsCount) { variants.getElementName(it) } }
	}

	private val serialNameByContent by lazy { serialNames.associateBy(contentName) }

	/** Every subtype's Minecraft name, e.g. `["enchantments", "damage", ...]`. */
	val contentNames get() = serialNames.map(contentName)

	private fun generatedDeserializer(typeName: String): DeserializationStrategy<T> {
		val serialName = serialNameByContent[normalize(typeName)] ?: normalize(typeName)
		return polymorphic.findPolymorphicSerializerOrNull(ModuleOnlyDecoder(EmptySerializersModule()), serialName)
			?: error("No subtype '$typeName' in $baseName")
	}

	/** Decode one already-split `{typeName -> content}` entry, for map-shaped consumers like `ItemStackSubPredicates`. */
	fun deserializeJsonElement(json: Json, typeName: String, element: JsonElement): T =
		json.decodeFromJsonElement(generatedDeserializer(typeName), element)

	private fun contentJson(jsonObject: JsonObject) = when (moveIntoProperty) {
		null -> buildJsonObject { copyAllFrom(jsonObject, outputName) }
		else -> jsonObject[moveIntoProperty]?.jsonObject ?: buildJsonObject {}
	}

	private fun contentNbt(nbtCompound: NbtCompound) = when (moveIntoProperty) {
		null -> nbt { nbtCompound.filterKeys { it != outputName }.forEach(::put) }
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
	private fun deserializeBareJson(decoder: JsonDecoder, element: JsonElement): T {
		val candidates = serialNames.map { name -> generatedDeserializer(contentName(name)) }
		return candidates.firstNotNullOfOrNull { serializer ->
			runCatching { decoder.json.decodeFromJsonElement(serializer, element) }.getOrNull()
		} ?: error("No subtype of $baseName can deserialize non-object JSON element: $element")
	}

	private fun deserializeBareNbt(decoder: NbtDecoder, tag: NbtTag): T {
		val candidates = serialNames.map { name -> generatedDeserializer(contentName(name)) }
		return candidates.firstNotNullOfOrNull { serializer ->
			runCatching { decoder.nbt.decodeFromNbtTag(serializer, tag) }.getOrNull()
		} ?: error("No subtype of $baseName can deserialize non-compound NBT element: $tag")
	}

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
				valueNbt.filterKeys { it != outputName }.forEach(::put)
			}

			else -> nbt {
				if (!skipOutputName) put(outputName, NbtString(outputClassName))
				if (!(skipEmptyOutput && valueNbt.isEmpty()))
					putNbtCompound(moveIntoProperty) { valueNbt.filterKeys { it != outputName }.forEach(::put) }
			}
		}

		encoder.encodeNbtTag(finalNbt)
	}

	@OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
	override fun serialize(encoder: Encoder, value: T) {
		require(encoder is JsonEncoder || encoder is NbtEncoder) { "PolymorphicTypeSerializer can only be serialized to Json or Nbt." }

		val actual = polymorphic.findPolymorphicSerializer(encoder, value)
		val outputClassName = namespaced(contentName(actual.descriptor.serialName))

		when (encoder) {
			is JsonEncoder -> serializeJson(outputClassName, encoder.json.encodeToJsonElement(actual, value), encoder)
			is NbtEncoder -> serializeNbt(outputClassName, encoder.nbt.encodeToNbtTag(actual, value), encoder)
		}
	}

	/** The Minecraft name (namespaced, per [contentName]) that [value] would serialize under. */
	@OptIn(InternalSerializationApi::class)
	fun getContentName(value: T): String {
		val actual = polymorphic.findPolymorphicSerializer(ModuleOnlyEncoder, value)
		return namespaced(contentName(actual.descriptor.serialName))
	}
}

// Minimal CompositeDecoder that only exposes a SerializersModule, so a SealedClassSerializer can resolve a subtype by
// name (findPolymorphicSerializerOrNull) without a real decoding session. Every other operation is unreachable there.
// Internal (not private) so EnumLikeSerializer's reflection-free path can reuse it.
@OptIn(ExperimentalSerializationApi::class)
internal class ModuleOnlyDecoder(override val serializersModule: SerializersModule) : CompositeDecoder {
	private fun fail(): Nothing = error("ModuleOnlyDecoder only provides a SerializersModule")
	override fun decodeBooleanElement(descriptor: SerialDescriptor, index: Int) = fail()
	override fun decodeByteElement(descriptor: SerialDescriptor, index: Int) = fail()
	override fun decodeCharElement(descriptor: SerialDescriptor, index: Int) = fail()
	override fun decodeShortElement(descriptor: SerialDescriptor, index: Int) = fail()
	override fun decodeIntElement(descriptor: SerialDescriptor, index: Int) = fail()
	override fun decodeLongElement(descriptor: SerialDescriptor, index: Int) = fail()
	override fun decodeFloatElement(descriptor: SerialDescriptor, index: Int) = fail()
	override fun decodeDoubleElement(descriptor: SerialDescriptor, index: Int) = fail()
	override fun decodeStringElement(descriptor: SerialDescriptor, index: Int) = fail()
	override fun decodeInlineElement(descriptor: SerialDescriptor, index: Int) = fail()
	override fun decodeElementIndex(descriptor: SerialDescriptor) = fail()
	override fun <T> decodeSerializableElement(
		descriptor: SerialDescriptor,
		index: Int,
		deserializer: DeserializationStrategy<T>,
		previousValue: T?
	) = fail()

	override fun <T : Any> decodeNullableSerializableElement(
		descriptor: SerialDescriptor,
		index: Int,
		deserializer: DeserializationStrategy<T?>,
		previousValue: T?
	) = fail()

	override fun endStructure(descriptor: SerialDescriptor) = fail()
}

// Minimal Encoder that only exposes a SerializersModule, for findPolymorphicSerializer calls (e.g. getContentName)
// that need to resolve a subtype from a value with no real encoding session. AbstractEncoder supplies every other
// Encoder/CompositeEncoder member with a default that's unreachable here (SealedClassSerializer resolves purely
// from `value::class` and never calls back into the encoder).
@OptIn(ExperimentalSerializationApi::class)
internal object ModuleOnlyEncoder : AbstractEncoder() {
	override val serializersModule: SerializersModule = EmptySerializersModule()
}
