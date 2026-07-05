package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.arguments.components.item.CustomComponent
import io.github.ayfri.kore.serializers.NbtAsJsonSerializer
import io.github.ayfri.kore.utils.unescape
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtDecoder
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
	// `[\s\S]` is used instead of `.` (with DOT_MATCHES_ALL) since that RegexOption is JVM-only.
	.replace(Regex("\"\'\"([\\s\\S]+?)\"\'\""), "'\"$1\"'")
	// we also need a fix for JSON Components as they are serialized as JSON but single quoted.
	.replace(Regex("\"\'\\{([\\s\\S]+?)\\}\'\""), "'{$1}'")

data object ComponentsSerializer : KSerializer<ComponentsScope> {
	override val descriptor = buildClassSerialDescriptor("Components") {
		element<Map<String, Component>>("components")
	}

	/**
	 * Decodes a components map generically: each entry becomes a raw [CustomComponent] holding the value as an [NbtTag],
	 * keyed by its component name. This always round-trips, but yields opaque components rather than their typed
	 * counterparts (e.g. [DamageComponent][io.github.ayfri.kore.arguments.components.item.DamageComponent]), because
	 * [Component] is not a sealed hierarchy and has no name to serializer registry to dispatch on.
	 */
	override fun deserialize(decoder: Decoder) = when (decoder) {
		is NbtDecoder -> {
			val compound = decoder.decodeNbtTag() as? NbtCompound ?: error("A components map must be an NBT compound.")
			Components(compound.mapValuesTo(mutableMapOf()) { (_, value) -> CustomComponent(value) })
		}

		is JsonDecoder -> {
			val json = decoder.decodeJsonElement() as? JsonObject ?: error("A components map must be a JSON object.")
			Components(json.mapValuesTo(mutableMapOf()) { (_, value) ->
				CustomComponent(decoder.json.decodeFromJsonElement(NbtAsJsonSerializer, value))
			})
		}

		else -> error("This serializer can only be used with Nbt or Json")
	}

	override fun serialize(encoder: Encoder, value: ComponentsScope) = when (encoder) {
		is NbtEncoder -> encoder.encodeNbtTag(value.asNbt())
		is JsonEncoder -> encoder.encodeJsonElement(value.asJson())
		else -> error("This serializer can only be used with Nbt or Json")
	}
}
