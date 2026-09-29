package io.github.ayfri.kore.utils

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.ClassDiscriminatorMode
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import kotlinx.serialization.modules.EmptySerializersModule
import kotlin.enums.EnumEntries

/**
 * Base serializer for enums that serialize to/from a transformed string representation.
 *
 * @param T The enum type.
 * @param values The enum entries.
 * @param encode Transform an enum value to its serialized string, decoding looks the string up the same way.
 */
open class EnumStringSerializer<T : Enum<T>>(
	private val values: EnumEntries<T>,
	private val encode: T.() -> String,
) : KSerializer<T> {
	override val descriptor = PrimitiveSerialDescriptor("EnumStringSerializer", PrimitiveKind.STRING)

	private val byEncoded = lazy { values.associateBy { it.encode() } }

	/** The entry serialized as [string], `null` when there is none. */
	protected open fun decode(string: String) = byEncoded.value[string]

	override fun deserialize(decoder: Decoder): T {
		val string = decoder.decodeString()
		return decode(string) ?: throw SerializationException("'$string' is not an entry of this enum.")
	}

	override fun serialize(encoder: Encoder, value: T) = encoder.encodeString(value.encode())
}

@OptIn(ExperimentalSerializationApi::class)
private val lazyJsonSerializer = lazy {
	Json {
		classDiscriminatorMode = ClassDiscriminatorMode.NONE
		encodeDefaults = false
		ignoreUnknownKeys = true
		namingStrategy = JsonNamingStrategy.SnakeCase
	}
}

/** The JSON format of chat and item components outside a data pack file: snake_case keys, no defaults, no class discriminator. */
val jsonSerializer get() = lazyJsonSerializer.value

/** Keeps the single value a serializer writes, so reading a value's serialized name builds no JSON tree. */
@OptIn(ExperimentalSerializationApi::class)
private class ArgEncoder : AbstractEncoder() {
	override val serializersModule = EmptySerializersModule()
	var result = ""

	override fun beginStructure(descriptor: SerialDescriptor) =
		throw SerializationException("${descriptor.serialName} doesn't serialize to a single value.")

	override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
		result = enumDescriptor.getElementName(index)
	}

	override fun encodeValue(value: Any) {
		result = value.toString()
	}
}

internal fun <T> argOf(serializer: SerializationStrategy<T>, value: T) = ArgEncoder().apply { encodeSerializableValue(serializer, value) }.result

/** The serialized form of this value, for command arguments like enum names. */
internal inline fun <reified T : @Serializable Any> T.asArg() = argOf(serializer<T>(), this)

/** The Minecraft name of a sealed subtype, the snake_case simple name of its [serialName]: `foo.BlendToGray` -> `blend_to_gray`. */
internal fun defaultContentName(serialName: String) = serialName.substringAfterLast('.').snakeCase()

/** The key [json] writes the [serialName] element of this descriptor under, after its naming strategy. */
@OptIn(ExperimentalSerializationApi::class)
internal fun SerialDescriptor.jsonKey(json: Json, serialName: String) =
	json.configuration.namingStrategy?.serialNameForJson(this, getElementIndex(serialName), serialName) ?: serialName
