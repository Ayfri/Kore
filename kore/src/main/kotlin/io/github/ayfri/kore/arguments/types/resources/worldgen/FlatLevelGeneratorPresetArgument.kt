package io.github.ayfri.kore.arguments.types.resources.worldgen

import io.github.ayfri.kore.arguments.Argument
import io.github.ayfri.kore.arguments.types.FlatLevelGeneratorPresetOrTagArgument
import io.github.ayfri.kore.arguments.types.ResourceLocationArgument
import kotlinx.serialization.Serializable

@Serializable(with = Argument.ArgumentSerializer::class)
interface FlatLevelGeneratorPresetArgument : ResourceLocationArgument, FlatLevelGeneratorPresetOrTagArgument {
	companion object {
		operator fun invoke(preset: String, namespace: String = "minecraft") = object : FlatLevelGeneratorPresetArgument {
			override val name = preset
			override val namespace = namespace
		}
	}
}
