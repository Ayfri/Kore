package arguments.types.resources.tagged

import arguments.Argument
import arguments.types.ItemOrTagArgument
import arguments.types.TaggedResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface ItemTagArgument : TaggedResourceLocationArgument, ItemOrTagArgument {
	companion object {
		operator fun invoke(tagName: String, namespace: String = "minecraft") = object : ItemTagArgument {
			override val name = tagName
			override val namespace = namespace
		}
	}
}
