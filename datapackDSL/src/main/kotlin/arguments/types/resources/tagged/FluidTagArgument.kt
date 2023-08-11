package arguments.types.resources.tagged

import arguments.Argument
import arguments.types.FluidOrTagArgument
import arguments.types.TaggedResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface FluidTagArgument : TaggedResourceLocationArgument, FluidOrTagArgument {
	companion object {
		operator fun invoke(blockOrTag: String, namespace: String = "minecraft") = object : FluidTagArgument {
			override val name = blockOrTag
			override val namespace = namespace
		}
	}
}
