package arguments.types.resources.tagged

import arguments.Argument
import arguments.types.BlockOrTagArgument
import arguments.types.TaggedResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface BlockTagArgument : TaggedResourceLocationArgument, BlockOrTagArgument {
	companion object {
		operator fun invoke(tagName: String, namespace: String = "minecraft") = object : BlockTagArgument {
			override val name = tagName
			override val namespace = namespace
		}
	}
}
