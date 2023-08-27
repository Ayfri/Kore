package arguments.types.resources.tagged

import arguments.Argument
import arguments.types.ConfiguredStructureOrTagArgument
import arguments.types.TaggedResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface ConfiguredStructureTagArgument : TaggedResourceLocationArgument, ConfiguredStructureOrTagArgument {
	companion object {
		operator fun invoke(tagName: String, namespace: String = "minecraft") = object : ConfiguredStructureTagArgument {
			override val name = tagName
			override val namespace = namespace
		}
	}
}
