package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
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
	this[ItemComponentTypes.JUKEBOX_PLAYABLE] = JukeboxPlayableComponent(song, showInTooltip)
}
