package arguments.types.resources.worldgen

import arguments.Argument
import arguments.types.ResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface FlatLevelGeneratorPresetArgument : ResourceLocationArgument {
	companion object {
		operator fun invoke(preset: String, namespace: String = "minecraft") = object : FlatLevelGeneratorPresetArgument {
			override val name = preset
			override val namespace = namespace
		}
	}
}
