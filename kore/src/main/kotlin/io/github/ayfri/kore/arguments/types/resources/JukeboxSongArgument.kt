package io.github.ayfri.kore.arguments.types.resources

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.types.JukeboxSongOrTagArgument
import io.github.ayfri.kore.arguments.types.ResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface JukeboxSongArgument : ResourceLocationArgument, JukeboxSongOrTagArgument {
	companion object {
		operator fun invoke(name: String, namespace: String = "minecraft") = object : JukeboxSongArgument {
			override val name = name
			override val namespace = namespace
		}
	}
}
