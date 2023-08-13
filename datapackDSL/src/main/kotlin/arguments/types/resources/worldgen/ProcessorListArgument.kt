package arguments.types.resources.worldgen

import arguments.Argument
import arguments.types.ResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface ProcessorListArgument : ResourceLocationArgument {
	companion object {
		operator fun invoke(feature: String, namespace: String = "minecraft") = object : ProcessorListArgument {
			override val name = feature
			override val namespace = namespace
		}
	}
}
