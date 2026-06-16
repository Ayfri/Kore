package io.github.ayfri.kore.features.timelines

import io.github.ayfri.kore.serializers.SinglePropertySimplifierSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A named tick position embedded inside a [Timeline] JSON file.
 *
 * Serializes as a plain integer when [showInCommands] is `null`, and as a full object otherwise:
 * - `TimelineMarker(ticks = 100)` → `100`
 * - `TimelineMarker(ticks = 100, showInCommands = true)` → `{ "ticks": 100, "show_in_commands": true }`
 *
 * This is the **definition** of the marker stored in the timeline file. To reference a marker
 * by name in a command, use [io.github.ayfri.kore.arguments.types.resources.TimeMarkerArgument].
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Timeline
 */
@Serializable(with = TimelineMarker.Companion.TimelineMarkerSerializer::class)
data class TimelineMarker(
	@SerialName("show_in_commands") var showInCommands: Boolean? = null,
	var ticks: Int,
) {
	companion object {
		data object TimelineMarkerSerializer : SinglePropertySimplifierSerializer<TimelineMarker, Int>(
			TimelineMarker::class,
			TimelineMarker::ticks,
		)
	}
}
