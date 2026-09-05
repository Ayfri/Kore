package io.github.ayfri.kore.utils

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonPrimitive
import kotlin.enums.EnumEntries

/**
 * Base serializer for enums that serialize to/from a transformed string representation.
 *
 * @param T The enum type.
 * @param values The enum entries.
 * @param encode Transform an enum value to its serialized string.
 * @param decode Find an enum value from a deserialized string (defaults to matching by [encode]).
 */
open class EnumStringSerializer<T : Enum<T>>(
	private val values: EnumEntries<T>,
	private val encode: T.() -> String,
	private val decode: (String) -> T = { str -> values.first { it.encode() == str } },
) : KSerializer<T> {
	override val descriptor = PrimitiveSerialDescriptor("EnumStringSerializer", PrimitiveKind.STRING)
	override fun deserialize(decoder: Decoder): T = decode(decoder.decodeString())
	override fun serialize(encoder: Encoder, value: T) = encoder.encodeString(value.encode())
}

internal inline fun <reified T : @Serializable Any> T.asArg() = Json.encodeToJsonElement(this@asArg).jsonPrimitive.content
