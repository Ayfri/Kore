package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.utils.unescape
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.ClassDiscriminatorMode
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonNamingStrategy
import net.benwoodworth.knbt.NbtEncoder
import net.benwoodworth.knbt.NbtTag

@OptIn(ExperimentalSerializationApi::class)
val jsonSerializer = Json {
	prettyPrint = false
	encodeDefaults = false
	classDiscriminatorMode = ClassDiscriminatorMode.NONE
	namingStrategy = JsonNamingStrategy.SnakeCase
}

/**
 * SNBT serialization quotes and escapes chat components (and single-quotes JSON ones); this unwraps such a value
 * back to the inline form expected inside the `item[key=value]` command syntax.
 *
 * Only used for components where [Component.isChatComponent] is `true`.
 */
internal fun NbtTag.unescapeChatComponent() = toString().unescape()
	// The quotes are added by the serializer, we just need to unescape the string.
	.replace(Regex("\"\'\"(.+?)\"\'\"", RegexOption.DOT_MATCHES_ALL), "'\"$1\"'")
	// we also need a fix for JSON Components as they are serialized as JSON but single quoted.
	.replace(Regex("\"\'\\{(.+?)\\}\'\"", RegexOption.DOT_MATCHES_ALL), "'{$1}'")

data object ComponentsSerializer : KSerializer<ComponentsScope> {
	override val descriptor = buildClassSerialDescriptor("Components") {
		element<Map<String, Component>>("components")
	}

	override fun deserialize(decoder: Decoder) = error("Components deserialization is not supported.")

	override fun serialize(encoder: Encoder, value: ComponentsScope) = when (encoder) {
		is NbtEncoder -> encoder.encodeNbtTag(value.asNbt())
		is JsonEncoder -> encoder.encodeJsonElement(value.asJson())
		else -> error("This serializer can only be used with Nbt or Json")
	}
}
