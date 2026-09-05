package io.github.ayfri.kore.features.timelines

import io.github.ayfri.kore.arguments.colors.Color
import io.github.ayfri.kore.features.worldgen.environmentattributes.types.ColorValue
import io.github.ayfri.kore.features.worldgen.environmentattributes.types.EnvironmentAttributesType
import kotlinx.serialization.Serializable

/**
 * A keyframe within an environment attribute track.
 *
 * @param ticks A value between 0 and period_ticks defining when this keyframe is active.
 * @param value The value for the attribute modifier, as a [KeyframeValue].
 */
@Serializable
data class EnvironmentAttributeKeyframe(
	var ticks: Int = 0,
	var value: KeyframeValue = KeyframeValue.FloatPrimitive(0f),
)

/** Sets the value of this keyframe to a float. */
fun EnvironmentAttributeKeyframe.value(value: Float) {
	this.value = KeyframeValue.FloatPrimitive(value)
}

/** Sets the value of this keyframe to an int. */
fun EnvironmentAttributeKeyframe.value(value: Int) {
	this.value = KeyframeValue.IntPrimitive(value)
}

/** Sets the value of this keyframe to a boolean. */
fun EnvironmentAttributeKeyframe.value(value: Boolean) {
	this.value = KeyframeValue.BooleanPrimitive(value)
}

/** Sets the value of this keyframe to a string. */
fun EnvironmentAttributeKeyframe.value(value: String) {
	this.value = KeyframeValue.StringPrimitive(value)
}

/** Sets the value of this keyframe to a [Color]. */
fun EnvironmentAttributeKeyframe.value(value: Color) {
	this.value = KeyframeValue.Typed(ColorValue(value))
}

/** Sets the value of this keyframe to an [EnvironmentAttributesType]. */
fun EnvironmentAttributeKeyframe.value(value: EnvironmentAttributesType) {
	this.value = KeyframeValue.Typed(value)
}
