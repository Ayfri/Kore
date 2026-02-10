package io.github.ayfri.kore.features.timelines

import io.github.ayfri.kore.utils.getSerialName
import io.github.ayfri.kore.utils.snakeCase
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

/**
 * Easing type used to interpolate between keyframes in a timeline track.
 *
 * Simple easing types serialize as plain strings (e.g., `"linear"`, `"in_back"`).
 * [CubicBezier] serializes as `{"cubic_bezier": [x1, y1, x2, y2]}`.
 */
@Serializable(with = EasingType.Companion.EasingTypeSerializer::class)
sealed class EasingType {
	companion object {
		data object EasingTypeSerializer : KSerializer<EasingType> {
			override val descriptor = buildClassSerialDescriptor("EasingType")

			override fun deserialize(decoder: Decoder) = error("EasingType cannot be deserialized")

			override fun serialize(encoder: Encoder, value: EasingType) {
				require(encoder is JsonEncoder)
				when (value) {
					is CubicBezier -> encoder.encodeJsonElement(buildJsonObject {
						putJsonArray("cubic_bezier") {
							add(value.x1)
							add(value.y1)
							add(value.x2)
							add(value.y2)
						}
					})

					else -> encoder.encodeJsonElement(JsonPrimitive(value::class.getSerialName().snakeCase()))
				}
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
