package arguments.types.resources.tagged

import arguments.Argument
import arguments.types.BlockOrTagArgument
import arguments.types.TaggedResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface BlockTagArgument : TaggedResourceLocationArgument, BlockOrTagArgument {
	companion object {
		operator fun invoke(blockOrTag: String, namespace: String = "minecraft") = object : BlockTagArgument {
			override val name = blockOrTag
			override val namespace = namespace
		}
	}
}
