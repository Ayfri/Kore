package io.github.ayfri.kore.features.timelines

import io.github.ayfri.kore.serializers.EnumLikeSerializer
import io.github.ayfri.kore.serializers.GeneratedSealedSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

/**
 * Easing type used to interpolate between keyframes in a timeline track.
 *
 * Simple easing types serialize as plain strings (e.g., `"linear"`, `"in_back"`).
 * [CubicBezier] serializes as `{"cubic_bezier": [x1, y1, x2, y2]}`.
 */
@GeneratedSealedSerializer
@Serializable(with = EasingType.Companion.EasingTypeSerializer::class)
sealed class EasingType {
	companion object {
		data object EasingTypeSerializer : KSerializer<EasingType> {
			private val simple = EnumLikeSerializer(easingTypeSealedSerializer())
			override val descriptor = simple.descriptor

			override fun deserialize(decoder: Decoder): EasingType {
				require(decoder is JsonDecoder) { "EasingType can only be deserialized from JSON." }
				return when (val element = decoder.decodeJsonElement()) {
					is JsonObject -> element.getValue("cubic_bezier").jsonArray.map { it.jsonPrimitive.float }
						.let { (x1, y1, x2, y2) -> CubicBezier(x1, y1, x2, y2) }

					else -> decoder.json.decodeFromJsonElement(simple, element)
				}
			}

			override fun serialize(encoder: Encoder, value: EasingType) = when (value) {
				is CubicBezier -> encoder.encodeSerializableValue(
					JsonObject.serializer(),
					JsonObject(mapOf("cubic_bezier" to JsonArray(listOf(value.x1, value.y1, value.x2, value.y2).map(::JsonPrimitive)))),
				)

				else -> simple.serialize(encoder, value)
			}
		}
	}
}

@Serializable
data object Constant : EasingType()

@Serializable
data object Linear : EasingType()

@Serializable
data object InBack : EasingType()

@Serializable
data object InBounce : EasingType()

@Serializable
data object InCirc : EasingType()

@Serializable
data object InCubic : EasingType()

@Serializable
data object InElastic : EasingType()

@Serializable
data object InExpo : EasingType()

@Serializable
data object InQuad : EasingType()

@Serializable
data object InQuart : EasingType()

@Serializable
data object InQuint : EasingType()

@Serializable
data object InSine : EasingType()

@Serializable
data object InOutBack : EasingType()

@Serializable
data object InOutBounce : EasingType()

@Serializable
data object InOutCirc : EasingType()

@Serializable
data object InOutCubic : EasingType()

@Serializable
data object InOutElastic : EasingType()

@Serializable
data object InOutExpo : EasingType()

@Serializable
data object InOutQuad : EasingType()

@Serializable
data object InOutQuart : EasingType()

@Serializable
data object InOutQuint : EasingType()

@Serializable
data object InOutSine : EasingType()

@Serializable
data object OutBack : EasingType()

@Serializable
data object OutBounce : EasingType()

@Serializable
data object OutCirc : EasingType()

@Serializable
data object OutCubic : EasingType()

@Serializable
data object OutElastic : EasingType()

@Serializable
data object OutExpo : EasingType()

@Serializable
data object OutQuad : EasingType()

@Serializable
data object OutQuart : EasingType()

@Serializable
data object OutQuint : EasingType()

@Serializable
data object OutSine : EasingType()

/**
 * Cubic bezier easing type with two control points.
 *
 * Serializes as `{"cubic_bezier": [x1, y1, x2, y2]}`.
 */
@Serializable
data class CubicBezier(
	var x1: Float,
	var y1: Float,
	var x2: Float,
	var y2: Float,
) : EasingType()
