package arguments.types.resources.worldgen

import arguments.Argument
import arguments.types.ResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface CarverArgument : ResourceLocationArgument {
	companion object {
		operator fun invoke(carver: String, namespace: String = "minecraft") = object : CarverArgument {
			override val name = carver
			override val namespace = namespace
		}
	}
}
