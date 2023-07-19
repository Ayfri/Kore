package arguments.types.resources.worldgen

import arguments.types.ResourceLocationArgument

interface FlatLevelGeneratorPresetArgument : ResourceLocationArgument {
	companion object {
		operator fun invoke(preset: String, namespace: String = "minecraft") = object : FlatLevelGeneratorPresetArgument {
			override val name = preset
			override val namespace = namespace
		}
	}
}
