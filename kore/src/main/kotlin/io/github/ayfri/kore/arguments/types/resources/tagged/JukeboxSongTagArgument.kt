package io.github.ayfri.kore.arguments.types.resources.tagged

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.types.JukeboxSongOrTagArgument
import io.github.ayfri.kore.arguments.types.TaggedResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface JukeboxSongTagArgument : TaggedResourceLocationArgument, JukeboxSongOrTagArgument {
	companion object {
		operator fun invoke(name: String, namespace: String = "minecraft") = object : JukeboxSongTagArgument {
			override val name = name
			override val namespace = namespace
		}
	}
}
