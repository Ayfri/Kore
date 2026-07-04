package io.github.ayfri.kore.features.timelines

import io.github.ayfri.kore.features.worldgen.environmentattributes.EnvironmentAttributeModifier
import kotlinx.serialization.Serializable

/**
 * An attribute track within a timeline, mapping keyframes to an environment attribute.
 *
 * @param ease The easing type used to interpolate between keyframes. Default is linear.
 * @param keyframes A list of keyframes for the track.
 * @param modifier The ID of an environment attribute modifier. Default is "override".
 */
@Serializable
data class EnvironmentAttributeTrack(
	var ease: EasingType? = null,
	var keyframes: MutableList<EnvironmentAttributeKeyframe> = mutableListOf(),
	var modifier: EnvironmentAttributeModifier? = null,
)

/**
 * Adds a keyframe to this track.
 */
fun EnvironmentAttributeTrack.keyframe(ticks: Int, init: EnvironmentAttributeKeyframe.() -> Unit) {
	keyframes += EnvironmentAttributeKeyframe(ticks = ticks).apply(init)
}
