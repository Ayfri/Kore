package io.github.ayfri.kore.arguments.types.resources

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.types.FluidOrTagArgument
import io.github.ayfri.kore.arguments.types.ResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface FluidArgument : ResourceLocationArgument, FluidOrTagArgument {
	companion object {
		operator fun invoke(fluid: String, namespace: String = "minecraft") = object : FluidArgument {
			override val name = fluid
			override val namespace = namespace
		}
	}
}
