package io.github.ayfri.kore.features.timelines

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.features.worldgen.environmentattributes.EnvironmentAttributeModifier
import io.github.ayfri.kore.generated.arguments.types.EnvironmentAttributeArgument
import io.github.ayfri.kore.generated.arguments.types.TimelineArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Data-driven timeline definition for Minecraft Java Edition.
 *
 * Timelines control game behavior and visuals based on the absolute day time
 * through environment attributes. They define tracks that map environment
 * attributes to keyframe-based animations with configurable easing.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/timelines
 * JSON format reference: https://minecraft.wiki/w/Timeline
 */
@Serializable
data class Timeline(
	@Transient
	override var fileName: String = "timeline",
	var periodTicks: Int? = null,
	var tracks: MutableMap<EnvironmentAttributeArgument, EnvironmentAttributeTrack>? = null,
) : Generator("timeline") {
	override fun generateJson(dataPack: DataPack) = dataPack.jsonEncoder.encodeToString(this)
}

/**
 * Create and register a timeline in this [DataPack].
 *
 * Produces `data/<namespace>/timeline/<fileName>.json`.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/timelines
 * JSON format reference: https://minecraft.wiki/w/Timeline
 */
fun DataPack.timeline(fileName: String = "timeline", init: Timeline.() -> Unit = {}): TimelineArgument {
	val timeline = Timeline(fileName).apply(init)
	timelines += timeline
	return TimelineArgument(fileName, timeline.namespace ?: name)
}

/**
 * Adds or configures a track for the given [attribute] in this timeline.
 */
fun Timeline.track(
	attribute: EnvironmentAttributeArgument,
	ease: EasingType? = null,
	modifier: EnvironmentAttributeModifier? = null,
	init: EnvironmentAttributeTrack.() -> Unit,
) {
	val tracks = tracks ?: mutableMapOf<EnvironmentAttributeArgument, EnvironmentAttributeTrack>().also { tracks = it }
	val track = tracks.getOrPut(attribute) { EnvironmentAttributeTrack(ease, modifier = modifier) }.apply(init)
	tracks[attribute] = track
}
