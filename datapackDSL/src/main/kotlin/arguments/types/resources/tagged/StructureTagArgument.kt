package arguments.types.resources.tagged

import arguments.Argument
import arguments.types.StructureOrTagArgument
import arguments.types.TaggedResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface StructureTagArgument : TaggedResourceLocationArgument, StructureOrTagArgument {
	companion object {
		operator fun invoke(tagName: String, namespace: String = "minecraft") = object : StructureTagArgument {
			override val name = tagName
			override val namespace = namespace
		}
	}
}
