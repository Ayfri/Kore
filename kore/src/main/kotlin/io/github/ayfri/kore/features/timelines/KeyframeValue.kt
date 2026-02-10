package io.github.ayfri.kore.features.timelines

import io.github.ayfri.kore.features.worldgen.environmentattributes.types.EnvironmentAttributesType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive

/**
 * Represents a keyframe value in a timeline track.
 *
 * Can hold either a typed [EnvironmentAttributesType] or a raw primitive value
 * (float, int, boolean, string) for custom or not-yet-implemented environment attributes.
 */
@Serializable(with = KeyframeValue.Companion.KeyframeValueSerializer::class)
sealed interface KeyframeValue {
	/** Wraps an [EnvironmentAttributesType] as a keyframe value. */
	data class Typed(val type: EnvironmentAttributesType) : KeyframeValue

	/** Wraps a float as a keyframe value. */
	data class FloatPrimitive(val value: Float) : KeyframeValue

	/** Wraps an int as a keyframe value. */
	data class IntPrimitive(val value: Int) : KeyframeValue

	/** Wraps a boolean as a keyframe value. */
	data class BooleanPrimitive(val value: Boolean) : KeyframeValue

	/** Wraps a string as a keyframe value. */
	data class StringPrimitive(val value: String) : KeyframeValue

	companion object {
		data object KeyframeValueSerializer : KSerializer<KeyframeValue> {
			override val descriptor = buildClassSerialDescriptor("KeyframeValue")

			override fun deserialize(decoder: Decoder) = error("KeyframeValue cannot be deserialized")

			override fun serialize(encoder: Encoder, value: KeyframeValue) {
				when (value) {
					is Typed -> encoder.encodeSerializableValue(
						EnvironmentAttributesType.Companion.EnvironmentAttributesTypeSerializer,
						value.type
					)

					is FloatPrimitive -> (encoder as JsonEncoder).encodeJsonElement(JsonPrimitive(value.value))
					is IntPrimitive -> (encoder as JsonEncoder).encodeJsonElement(JsonPrimitive(value.value))
					is BooleanPrimitive -> (encoder as JsonEncoder).encodeJsonElement(JsonPrimitive(value.value))
					is StringPrimitive -> (encoder as JsonEncoder).encodeJsonElement(JsonPrimitive(value.value))
				}
			}
		}
	}
}
