package io.github.ayfri.kore.arguments.components

import io.github.ayfri.kore.arguments.components.item.CustomComponent
import io.github.ayfri.kore.serializers.NbtAsJsonSerializer
import io.github.ayfri.kore.serializers.nbtDecoder
import io.github.ayfri.kore.serializers.nbtEncoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtTag

data object ComponentsSerializer : KSerializer<ComponentsScope> {
	override val descriptor = buildClassSerialDescriptor("Components") {
		element(
			"components",
			MapSerializer(String.serializer(), Component.Companion.ComponentSerializer()).descriptor,
		)
	}

	/**
	 * Decodes a components map generically: each entry becomes a raw [CustomComponent] holding the value as an [NbtTag],
	 * keyed by its component name. This always round-trips, but yields opaque components rather than their typed
	 * counterparts (e.g. [DamageComponent][io.github.ayfri.kore.arguments.components.item.DamageComponent]), because
	 * [Component] is not a sealed hierarchy and has no name to serializer registry to dispatch on.
	 */
	override fun deserialize(decoder: Decoder) = when (decoder) {
		is JsonDecoder -> {
			val json = decoder.decodeJsonElement() as? JsonObject ?: error("A components map must be a JSON object.")
			Components(json.mapValuesTo(mutableMapOf()) { (_, value) ->
				CustomComponent(decoder.json.decodeFromJsonElement(NbtAsJsonSerializer, value))
			})
		}

		else -> {
			val compound = (decoder.nbtDecoder ?: error("This serializer can only be used with Nbt or Json")).decodeNbtTag() as? NbtCompound
				?: error("A components map must be an NBT compound.")
			Components(compound.mapValuesTo(mutableMapOf()) { (_, value) -> CustomComponent(value) })
		}
	}

	override fun serialize(encoder: Encoder, value: ComponentsScope) = when (encoder) {
		is JsonEncoder -> encoder.encodeJsonElement(value.asJson())
		else -> encoder.nbtEncoder?.encodeNbtTag(value.asNbt()) ?: error("This serializer can only be used with Nbt or Json")
	}
}
