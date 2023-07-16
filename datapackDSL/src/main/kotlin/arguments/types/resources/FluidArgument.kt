package arguments.types.resources

import arguments.Argument
import arguments.types.ResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface FluidArgument : ResourceLocationArgument {
	companion object {
		operator fun invoke(fluid: String, namespace: String = "minecraft") = object : FluidArgument {
			override val name = fluid
			override val namespace = namespace
		}
	}
}
