package io.github.ayfri.kore.utils

import io.github.ayfri.kore.serializers.JsonSerialName
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import kotlin.enums.EnumEntries
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.full.createType
import kotlin.reflect.full.findAnnotation

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

fun KAnnotatedElement.getSerialName() = findAnnotation<SerialName>()?.value ?: findAnnotation<JsonSerialName>()?.name

fun KClass<*>.getSerialName() = (this as KAnnotatedElement).getSerialName() ?: simpleName ?: ""

/**
 * Resolves the runtime serializer for [value]'s concrete class.
 *
 * Used to serialize abstract types ([Component][io.github.ayfri.kore.arguments.components.Component],
 * [ChatComponent][io.github.ayfri.kore.arguments.chatcomponents.ChatComponent]) polymorphically without a
 * discriminator: each subclass writes its own structure, so the subclass serializer must be looked up by class.
 */
@Suppress("UNCHECKED_CAST")
fun <T : Any> SerializersModule.serializerFor(value: T) = serializer(value::class.createType()) as KSerializer<T>
