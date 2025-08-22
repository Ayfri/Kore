package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.JukeboxSongArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable

@Serializable(with = JukeboxPlayableComponent.Companion.JukeboxPlayableComponentSerializer::class)
data class JukeboxPlayableComponent(var song: JukeboxSongArgument) : Component() {
	companion object {
		data object JukeboxPlayableComponentSerializer : InlineAutoSerializer<JukeboxPlayableComponent>(JukeboxPlayableComponent::class)
	}
}

fun ComponentsScope.jukeboxPlayable(song: JukeboxSongArgument) = apply {
	this[ItemComponentTypes.JUKEBOX_PLAYABLE] = JukeboxPlayableComponent(song)
}
