package arguments.types.resources.tagged

import arguments.Argument
import arguments.types.FlatLevelGeneratorPresetOrTagArgument
import arguments.types.TaggedResourceLocationArgument
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
