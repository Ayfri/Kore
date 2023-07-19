package arguments.types.resources.worldgen

import arguments.Argument
import arguments.types.ConfiguredStructureOrTagArgument
import arguments.types.ResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface ConfiguredStructureArgument : ResourceLocationArgument, ConfiguredStructureOrTagArgument {
	companion object {
		operator fun invoke(name: String, namespace: String = "minecraft") = object : ConfiguredStructureArgument {
			override val name = name
			override val namespace = namespace
		}
	}
}
