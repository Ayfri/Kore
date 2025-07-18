package io.github.ayfri.kore.arguments.components.types

import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ComponentTypes
import io.github.ayfri.kore.generated.arguments.types.JukeboxSongArgument
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JukeboxPlayableComponent(
	var song: JukeboxSongArgument,
	@SerialName("show_in_tooltip")
	var showInTooltip: Boolean = true,
) : Component()

fun ComponentsScope.jukeboxPlayable(song: JukeboxSongArgument, showInTooltip: Boolean = true) = apply {
	this[ComponentTypes.JUKEBOX_PLAYABLE] = JukeboxPlayableComponent(song, showInTooltip)
}
