package io.github.ayfri.kore.arguments.components.matchers

import io.github.ayfri.kore.features.predicates.sub.item.ItemStackSubPredicates
import io.github.ayfri.kore.generated.arguments.types.JukeboxSongArgument
import io.github.ayfri.kore.serializers.InlinableList
import kotlinx.serialization.Serializable

@Serializable
data class JukeboxPlayableComponentMatcher(
	var song: InlinableList<JukeboxSongArgument> = emptyList(),
) : ComponentMatcher()

fun ItemStackSubPredicates.jukeboxPlayable(init: JukeboxPlayableComponentMatcher.() -> Unit) =
	apply { matchers += JukeboxPlayableComponentMatcher().apply(init) }

fun JukeboxPlayableComponentMatcher.songs(vararg songs: JukeboxSongArgument) {
	song = songs.toList()
}
