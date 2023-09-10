package io.github.ayfri.kore.arguments.types.resources.tagged

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.types.TaggedResourceLocationArgument
import io.github.ayfri.kore.arguments.types.WorldPresetOrTagArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface WorldPresetTagArgument : TaggedResourceLocationArgument, WorldPresetOrTagArgument {
	companion object {
		operator fun invoke(tagName: String, namespace: String = "minecraft") = object : WorldPresetTagArgument {
			override val name = tagName
			override val namespace = namespace
		}
	}
}
