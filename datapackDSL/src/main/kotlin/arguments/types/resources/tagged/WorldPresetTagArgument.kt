package arguments.types.resources.tagged

import arguments.Argument
import arguments.types.TaggedResourceLocationArgument
import arguments.types.WorldPresetOrTagArgument
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
