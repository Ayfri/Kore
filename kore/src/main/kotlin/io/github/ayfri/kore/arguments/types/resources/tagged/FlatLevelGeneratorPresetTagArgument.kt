package io.github.ayfri.kore.arguments.types.resources.tagged

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.types.FlatLevelGeneratorPresetOrTagArgument
import io.github.ayfri.kore.arguments.types.TaggedResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface FlatLevelGeneratorPresetTagArgument : TaggedResourceLocationArgument, FlatLevelGeneratorPresetOrTagArgument {
	companion object {
		operator fun invoke(tagName: String, namespace: String = "minecraft") = object : FlatLevelGeneratorPresetTagArgument {
			override val name = tagName
			override val namespace = namespace
		}
	}
}
