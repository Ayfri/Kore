package io.github.ayfri.kore.features.timelines

import io.github.ayfri.kore.DataPack
import io.github.ayfri.kore.Generator
import io.github.ayfri.kore.features.worldgen.environmentattributes.EnvironmentAttributeModifier
import io.github.ayfri.kore.generated.arguments.types.EnvironmentAttributeArgument
import io.github.ayfri.kore.generated.arguments.types.TimelineArgument
import io.github.ayfri.kore.generated.arguments.types.WorldClockArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Data-driven timeline definition for Minecraft Java Edition.
 *
 * Timelines control game behavior and visuals based on a world clock through environment
 * attributes. They define tracks that map environment attributes to keyframe-based animations
 * with configurable easing, optional named time-markers, and a configurable period.
 *
 * Docs: https://kore.ayfri.com/docs/data-driven/timelines
 * JSON format reference: https://minecraft.wiki/w/Timeline
 */
@Serializable
data class Timeline(
	@Transient
	override var fileName: String = "timeline",
	var clock: WorldClockArgument,
	var periodTicks: Int? = null,
	var timeMarkers: MutableMap<String, TimelineMarker>? = null,
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
fun DataPack.timeline(
	fileName: String = "timeline",
	clock: WorldClockArgument,
	init: Timeline.() -> Unit = {},
): TimelineArgument {
	val timeline = Timeline(fileName = fileName, clock = clock).apply(init)
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
	val mutableTracks =
		tracks ?: mutableMapOf<EnvironmentAttributeArgument, EnvironmentAttributeTrack>().also { tracks = it }
	val track = mutableTracks.getOrPut(attribute) { EnvironmentAttributeTrack(ease, modifier = modifier) }.apply(init)
	mutableTracks[attribute] = track
}

/**
 * Adds or updates a named time-marker in this timeline.
 *
 * @param name Identifier for this marker (used in `/time query <timeline>` commands)
 * @param ticks Position of the marker in ticks
 * @param showInCommands When non-null, controls whether the marker appears in command suggestions
 */
fun Timeline.timeMarker(name: String, ticks: Int, showInCommands: Boolean? = null) {
	val mutableMarkers = timeMarkers ?: mutableMapOf<String, TimelineMarker>().also { timeMarkers = it }
	mutableMarkers[name] = TimelineMarker(showInCommands, ticks)
}
