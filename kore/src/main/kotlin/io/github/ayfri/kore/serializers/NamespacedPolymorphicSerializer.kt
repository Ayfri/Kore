package io.github.ayfri.kore.serializers

import io.github.ayfri.kore.utils.*
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.internal.AbstractPolymorphicSerializer
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import net.benwoodworth.knbt.*
import kotlin.reflect.KClass
import kotlin.reflect.full.createType
import kotlin.reflect.full.hasAnnotation

private fun defaultContentName(serialName: String) = serialName.substringAfterLast('.').snakeCase()

/**
 * Serializes a sealed hierarchy as Minecraft's `{ "type": "<namespace:name>", ...fields }` shape, resolving every case
 * automatically - no case list to maintain. Non-object cases (a subtype that serializes to a bare array or primitive)
 * are emitted as-is, which is why this exists instead of kotlinx's built-in polymorphism.
 *
 * Two ways to construct it:
 *
 * - **Preferred, reflection-free** - pass the plugin-generated sealed serializer. Only works when the base sealed class
 *   is a plain `@Serializable` (not `@Serializable(with = ...)`), so `serializer()` yields the [SealedClassSerializer]
 *   rather than this custom one. Use it when the base type is never auto-serialized as a nested property (it is always
 *   routed through this serializer explicitly), e.g. `ComponentMatcher`:
 *   ```kotlin
 *   @Serializable
 *   sealed class Foo {
 *       companion object {
 *           data object FooSerializer : NamespacedPolymorphicSerializer<Foo>(serializer())
 *       }
 *   }
 *   ```
 * - **Legacy, reflection-based** - pass `Foo::class`. Needed when the base is `@Serializable(with = FooSerializer)`
 *   (so nested `Foo` properties pick this format automatically). Getting the generated sealed serializer reflection-free
 *   is impossible there (`@KeepGeneratedSerializer` is banned on sealed classes, `serializer()` would recurse), so these
 *   stay on `sealedSubclasses`/`createType` until `generation/` emits a subtype registry per family.
 */
@OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
open class NamespacedPolymorphicSerializer<T : Any> private constructor(
	private val kClass: KClass<T>?,
	generated: KSerializer<T>?,
	private val outputName: String,
	private val skipOutputName: Boolean,
	private val moveIntoProperty: String?,
	private val useMinecraftPrefix: Boolean,
	private val skipEmptyOutput: Boolean,
	private val contentName: (String) -> String,
) : KSerializer<T> {
	constructor(
		kClass: KClass<T>,
		outputName: String = "type",
		skipOutputName: Boolean = false,
		moveIntoProperty: String? = null,
		useMinecraftPrefix: Boolean = true,
		skipEmptyOutput: Boolean = true,
	) : this(
		kClass,
		null,
		outputName,
		skipOutputName,
		moveIntoProperty,
		useMinecraftPrefix,
		skipEmptyOutput,
		::defaultContentName
	)

	constructor(
		generated: KSerializer<T>,
		outputName: String = "type",
		skipOutputName: Boolean = false,
		moveIntoProperty: String? = null,
		useMinecraftPrefix: Boolean = true,
		skipEmptyOutput: Boolean = true,
		contentName: (String) -> String = ::defaultContentName,
	) : this(
		null,
		generated,
		outputName,
		skipOutputName,
		moveIntoProperty,
		useMinecraftPrefix,
		skipEmptyOutput,
		contentName
	)

	@OptIn(InternalSerializationApi::class)
	private val polymorphic = generated as AbstractPolymorphicSerializer<T>?

	override val descriptor = serialDescriptor<JsonElement>()

	private val baseName get() = polymorphic?.descriptor?.serialName ?: kClass?.simpleName ?: "polymorphic"
	private fun namespaced(name: String) = if (useMinecraftPrefix) "minecraft:$name" else name
	private fun normalize(typeName: String) =
		if (useMinecraftPrefix) typeName.removePrefix("minecraft:") else typeName

	// region reflection-free path (generated sealed serializer)

	@OptIn(ExperimentalSerializationApi::class)
	private val serialNames: List<String> by lazy {
		val descriptor = polymorphic?.descriptor ?: return@lazy emptyList()
		if (descriptor.kind != PolymorphicKind.SEALED) return@lazy emptyList()
		descriptor.getElementDescriptor(1)
			.let { variants -> List(variants.elementsCount) { variants.getElementName(it) } }
	}

	private val serialNameByContent by lazy { serialNames.associateBy(contentName) }

	/** Every subtype's Minecraft name, e.g. `["enchantments", "damage", ...]`. Reflection-free path only. */
	val contentNames get() = serialNames.map(contentName)

	@OptIn(InternalSerializationApi::class)
	private fun generatedDeserializer(typeName: String): DeserializationStrategy<T> {
		val serialName = serialNameByContent[normalize(typeName)] ?: normalize(typeName)
		return polymorphic!!.findPolymorphicSerializerOrNull(ModuleOnlyDecoder(EmptySerializersModule()), serialName)
			?: error("No subtype '$typeName' in $baseName")
	}

	/** Decode one already-split `{typeName -> content}` entry, for map-shaped consumers like `ItemStackSubPredicates`. */
	fun deserializeJsonElement(json: Json, typeName: String, element: JsonElement): T =
		json.decodeFromJsonElement(generatedDeserializer(typeName), element)

	// endregion

	@OptIn(ExperimentalSerializationApi::class)
	private fun reflectiveDeserializer(module: SerializersModule, typeName: String): DeserializationStrategy<T> {
		val subclass = findSubclass(typeName)
		@Suppress("UNCHECKED_CAST")
		return module.getPolymorphic(kClass!!, typeName)
			?: module.serializer(subclass.createType()) as DeserializationStrategy<T>
	}

	private fun findSubclass(typeName: String): KClass<out T> {
		val name = normalize(typeName)
		return kClass!!.sealedSubclasses.firstOrNull { subclass ->
			val serialName =
				subclass.getSerialName().let { if (subclass.hasAnnotation<SerialName>()) it else it.snakeCase() }
			serialName == name
		} ?: error("No subclass of ${kClass.simpleName} found for type '$typeName'")
	}

	private fun objectDeserializer(module: SerializersModule, typeName: String) =
		if (polymorphic != null) generatedDeserializer(typeName) else reflectiveDeserializer(module, typeName)

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
					decoder.json.decodeFromJsonElement(
						objectDeserializer(decoder.json.serializersModule, typeName),
						contentJson(element)
					)
				}

				else -> deserializeBareJson(decoder, element)
			}

			is NbtDecoder -> when (val tag = decoder.decodeNbtTag()) {
				is NbtCompound -> {
					val typeName = tag[outputName]?.let { (it as NbtString).value }
						?: error("Missing '$outputName' field in NBT compound for $baseName")
					decoder.nbt.decodeFromNbtTag(
						objectDeserializer(decoder.nbt.serializersModule, typeName),
						contentNbt(tag)
					)
				}

				else -> deserializeBareNbt(decoder, tag)
			}

			else -> error("Unsupported decoder type")
		}
	}

	// A bare (non-object) element has no discriminator to read, so try every subtype until one decodes it.
	@OptIn(ExperimentalSerializationApi::class)
	private fun deserializeBareJson(decoder: JsonDecoder, element: JsonElement): T {
		val candidates = polymorphic?.let { serialNames.map { name -> generatedDeserializer(contentName(name)) } }
			?: kClass!!.sealedSubclasses.mapNotNull { subclass ->
				decoder.json.serializersModule.getPolymorphic(kClass, subclass.simpleName!!)
					?: runCatching { decoder.json.serializersModule.serializer(subclass.createType()) }.getOrNull()
			}

		return candidates.firstNotNullOfOrNull { serializer ->
			@Suppress("UNCHECKED_CAST")
			runCatching {
				decoder.json.decodeFromJsonElement(
					serializer as DeserializationStrategy<T>,
					element
				)
			}.getOrNull()
		} ?: error("No subtype of $baseName can deserialize non-object JSON element: $element")
	}

	@OptIn(ExperimentalSerializationApi::class)
	private fun deserializeBareNbt(decoder: NbtDecoder, tag: NbtTag): T {
		val candidates = polymorphic?.let { serialNames.map { name -> generatedDeserializer(contentName(name)) } }
			?: kClass!!.sealedSubclasses.mapNotNull { subclass ->
				decoder.nbt.serializersModule.getPolymorphic(kClass, subclass.simpleName!!)
					?: runCatching { decoder.nbt.serializersModule.serializer(subclass.createType()) }.getOrNull()
			}

		return candidates.firstNotNullOfOrNull { serializer ->
			@Suppress("UNCHECKED_CAST")
			runCatching { decoder.nbt.decodeFromNbtTag(serializer as DeserializationStrategy<T>, tag) }.getOrNull()
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

		val (serializer, outputClassName) = when {
			polymorphic != null -> {
				val actual = polymorphic.findPolymorphicSerializer(encoder, value)
				actual to namespaced(contentName(actual.descriptor.serialName))
			}

			else -> {
				require(kClass!!.isInstance(value) && value::class != kClass) { "Value must be instance of ${kClass.simpleName}" }
				(encoder.serializersModule.getPolymorphic(kClass, value) ?: encoder.serializersModule.serializerFor(
					value
				)) to getContentName(value)
			}
		}

		when (encoder) {
			is JsonEncoder -> serializeJson(
				outputClassName,
				encoder.json.encodeToJsonElement(serializer, value),
				encoder
			)

			is NbtEncoder -> serializeNbt(outputClassName, encoder.nbt.encodeToNbtTag(serializer, value), encoder)
		}
	}

	fun getContentName(value: T): String {
		val outputClassName = value::class.getSerialName().let {
			if (value::class.hasAnnotation<SerialName>()) it else it.snakeCase()
		}
		return namespaced(outputClassName)
	}
}

// Minimal CompositeDecoder that only exposes a SerializersModule, so a SealedClassSerializer can resolve a subtype by
// name (findPolymorphicSerializerOrNull) without a real decoding session. Every other operation is unreachable there.
@OptIn(ExperimentalSerializationApi::class)
private class ModuleOnlyDecoder(override val serializersModule: SerializersModule) : CompositeDecoder {
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
