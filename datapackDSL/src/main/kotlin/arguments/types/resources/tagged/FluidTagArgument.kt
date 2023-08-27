package arguments.types.resources.tagged

import arguments.Argument
import arguments.types.FluidOrTagArgument
import arguments.types.TaggedResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface FluidTagArgument : TaggedResourceLocationArgument, FluidOrTagArgument {
	companion object {
		operator fun invoke(tagName: String, namespace: String = "minecraft") = object : FluidTagArgument {
			override val name = tagName
			override val namespace = namespace
		}
	}
}
