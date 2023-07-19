package arguments.types.resources.worldgen

import arguments.Argument
import arguments.types.ResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface DensityFunctionArgument : ResourceLocationArgument {
	companion object {
		operator fun invoke(densityFunctionType: String, namespace: String = "minecraft") = object : DensityFunctionArgument {
			override val name = densityFunctionType
			override val namespace = namespace
		}
	}
}
