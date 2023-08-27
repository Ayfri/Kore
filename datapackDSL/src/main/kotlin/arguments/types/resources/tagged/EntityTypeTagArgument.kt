package arguments.types.resources.tagged

import arguments.Argument
import arguments.types.EntityTypeOrTagArgument
import arguments.types.TaggedResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface EntityTypeTagArgument : TaggedResourceLocationArgument, EntityTypeOrTagArgument {
	companion object {
		operator fun invoke(tagName: String, namespace: String = "minecraft") = object : EntityTypeTagArgument {
			override val name = tagName
			override val namespace = namespace
		}
	}
}
