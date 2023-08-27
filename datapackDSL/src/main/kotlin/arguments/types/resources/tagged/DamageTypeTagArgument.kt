package arguments.types.resources.tagged

import arguments.Argument
import arguments.types.DamageTypeOrTagArgument
import arguments.types.TaggedResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface DamageTypeTagArgument : TaggedResourceLocationArgument, DamageTypeOrTagArgument {
	companion object {
		operator fun invoke(tagName: String, namespace: String = "minecraft") = object : DamageTypeTagArgument {
			override val name = tagName
			override val namespace = namespace
		}
	}
}
