package io.github.ayfri.kore.utils

import io.github.ayfri.kore.serializers.JsonNamingSnakeCaseStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.ClassDiscriminatorMode
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
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

/**
 * The Minecraft name of a sealed subtype, the snake_case simple name of its [serialName] without [suffix]:
 * `foo.BlendToGray` -> `blend_to_gray`, `foo.UniformIntProvider` with the suffix `IntProvider` -> `uniform`.
 */
internal fun defaultContentName(serialName: String, suffix: String = "") =
	serialName.substringAfterLast('.').removeSuffix(suffix).snakeCase()

/** The serial names of a sealed family by [contentName], failing when two subtypes are written under the same name. */
internal fun Set<String>.associateByContentName(contentName: (String) -> String) =
	associateBy(contentName).also { byName ->
		check(byName.size == size) {
			val shared = groupBy(contentName).filterValues { it.size > 1 }
			"Several subtypes are written under the same name, add a @SerialName to one of them: $shared"
		}
	}

/** The key [json] writes the [serialName] element of this descriptor under, after its naming strategy. */
internal fun SerialDescriptor.jsonKey(json: Json, serialName: String) =
	json.configuration.namingStrategy?.serialNameForJson(this, getElementIndex(serialName), serialName) ?: serialName

/** The key SNBT writes the [serialName] element of this descriptor under, snake_case like the data pack JSON. */
internal fun SerialDescriptor.snakeCaseKey(serialName: String) =
	JsonNamingSnakeCaseStrategy.serialNameForJson(this, getElementIndex(serialName), serialName)
