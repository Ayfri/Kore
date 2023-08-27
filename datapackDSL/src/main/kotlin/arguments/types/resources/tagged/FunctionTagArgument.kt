package arguments.types.resources.tagged

import arguments.Argument
import arguments.types.FunctionOrTagArgument
import arguments.types.TaggedResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface FunctionTagArgument : TaggedResourceLocationArgument, FunctionOrTagArgument {
	companion object {
		operator fun invoke(tagName: String, namespace: String = "minecraft") = object : FunctionTagArgument {
			override val name = tagName
			override val namespace = namespace
		}
	}
}
