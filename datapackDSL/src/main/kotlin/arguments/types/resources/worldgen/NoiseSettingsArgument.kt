package arguments.types.resources.worldgen

import arguments.Argument
import arguments.types.ResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface NoiseSettingsArgument : ResourceLocationArgument {
	companion object {
		operator fun invoke(name: String, namespace: String = "minecraft") = object : NoiseSettingsArgument {
			override val name = name
			override val namespace = namespace
		}
	}
}
