package io.github.ayfri.kore.arguments.types.resources

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.types.ResourceLocationArgument
import kotlinx.serialization.Serializable

/**
 * Reference to a named time-marker defined inside a timeline JSON file.
 *
 * Time markers are named tick positions defined as [TimelineMarker][io.github.ayfri.kore.features.timelines.TimelineMarker] entries inside a [Timeline][io.github.ayfri.kore.features.timelines.Timeline].
 * This argument type is used wherever a command or condition accepts a `<timeMarker>` resource location,
 * e.g. `/time set <timeMarker>` and `/time of <clock> set <timeMarker>`.
 *
 * Minecraft Wiki: https://minecraft.wiki/w/Timeline
 */
@Serializable(with = Argument.ArgumentSerializer::class)
interface TimeMarkerArgument : ResourceLocationArgument {
	companion object {
		operator fun invoke(name: String, namespace: String = "minecraft") = object : TimeMarkerArgument {
			override val name = name
			override val namespace = namespace
		}
	}
}

/** Creates a [TimeMarkerArgument] for a named marker inside a timeline. */
fun timeMarker(name: String, namespace: String = "minecraft") = TimeMarkerArgument(name, namespace)
