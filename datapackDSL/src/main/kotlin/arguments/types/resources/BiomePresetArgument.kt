package arguments.types.resources

import arguments.Argument
import arguments.types.ResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface BiomePresetArgument : ResourceLocationArgument {
	companion object {
		operator fun invoke(preset: String, namespace: String = "minecraft") = object : BiomePresetArgument {
			override val name = preset
			override val namespace = namespace
		}
	}
}
