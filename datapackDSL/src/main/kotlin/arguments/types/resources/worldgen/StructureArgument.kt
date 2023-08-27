package arguments.types.resources.worldgen

import arguments.Argument
import arguments.types.ResourceLocationArgument
import arguments.types.StructureOrTagArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface StructureArgument : ResourceLocationArgument, StructureOrTagArgument {
	companion object {
		operator fun invoke(structure: String, namespace: String = "minecraft") = object : StructureArgument {
			override val name = structure
			override val namespace = namespace
		}
	}
}
