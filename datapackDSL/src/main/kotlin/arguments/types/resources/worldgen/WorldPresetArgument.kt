package arguments.types.resources.worldgen

import arguments.types.ResourceLocationArgument
import arguments.types.WorldPresetOrTagArgument

interface WorldPresetArgument : ResourceLocationArgument, WorldPresetOrTagArgument {
	companion object {
		operator fun invoke(worldPreset: String, namespace: String = "minecraft") = object : WorldPresetArgument {
			override val name = worldPreset
			override val namespace = namespace
		}
	}
}
