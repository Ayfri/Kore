package io.github.ayfri.kore.arguments.types.resources.worldgen

import io.github.ayfri.kore.arguments.types.ResourceLocationArgument
import io.github.ayfri.kore.arguments.types.WorldPresetOrTagArgument

interface WorldPresetArgument : ResourceLocationArgument, WorldPresetOrTagArgument {
	companion object {
		operator fun invoke(worldPreset: String, namespace: String = "minecraft") = object : WorldPresetArgument {
			override val name = worldPreset
			override val namespace = namespace
		}
	}
}
