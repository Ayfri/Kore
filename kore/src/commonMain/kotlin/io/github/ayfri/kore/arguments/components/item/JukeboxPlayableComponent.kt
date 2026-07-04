package io.github.ayfri.kore.arguments.components.item

import io.github.ayfri.kore.arguments.components.Component
import io.github.ayfri.kore.arguments.components.ComponentsScope
import io.github.ayfri.kore.generated.ItemComponentTypes
import io.github.ayfri.kore.generated.arguments.types.JukeboxSongArgument
import io.github.ayfri.kore.serializers.InlineAutoSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

/**
 * Represents the `minecraft:jukebox_playable` item component, which allows the item to be played in a jukebox with a specified music disc track.
 *
 * Serializes as the jukebox song id directly (inlined).
 *
 * Docs: https://kore.ayfri.com/docs/concepts/components
 * Minecraft Wiki: https://minecraft.wiki/w/Data_component_format#jukebox_playable
 */
@Serializable(with = JukeboxPlayableComponent.Companion.JukeboxPlayableComponentSerializer::class)
data class JukeboxPlayableComponent(var song: JukeboxSongArgument) : Component() {
	companion object {
		data object JukeboxPlayableComponentSerializer :
			InlineAutoSerializer<JukeboxPlayableComponent, JukeboxSongArgument>(
				serializer<JukeboxSongArgument>(),
				JukeboxPlayableComponent::song,
				::JukeboxPlayableComponent
			)
	}
}

/** Allows the item to be played in a jukebox with a specified music disc track. */
fun ComponentsScope.jukeboxPlayable(song: JukeboxSongArgument) = apply {
	this[ItemComponentTypes.JUKEBOX_PLAYABLE] = JukeboxPlayableComponent(song)
}
