package arguments.types.resources.tagged

import arguments.Argument
import arguments.types.ItemOrTagArgument
import arguments.types.TaggedResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface ItemTagArgument : TaggedResourceLocationArgument, ItemOrTagArgument {
	companion object {
		operator fun invoke(blockOrTag: String, namespace: String = "minecraft") = object : ItemTagArgument {
			override val name = blockOrTag
			override val namespace = namespace
		}
	}
}
