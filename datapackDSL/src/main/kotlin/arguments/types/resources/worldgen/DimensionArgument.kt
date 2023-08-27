package arguments.types.resources.worldgen

import arguments.Argument
import arguments.types.ResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface DimensionArgument : ResourceLocationArgument {
	companion object {
		operator fun invoke(dimension: String, namespace: String = "minecraft") = object : DimensionArgument {
			override val name = dimension
			override val namespace = namespace
		}
	}
}
